<!DOCTYPE HTML>
<html>
<head>
  <title>Results</title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link href="css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" type="text/css" href="results-style.css">
  <script src='jquery-2.1.0.min.js'></script>
  <script type="text/javascript">
  $(window).scroll(function(e) {
      var scroller_anchor = $(".scroller_anchor").offset().top;
       
      if ($(this).scrollTop() >= scroller_anchor && $('.scroller').css('position') != 'fixed') {
          $('.scroller').css({
              'background': 'rgba(255,255,255,0.95)',
              'position': 'fixed',
              'top': '0px'
          });
          $('.scroller_anchor').css('height', '50px');
      }
      else if ($(this).scrollTop() < scroller_anchor && $('.scroller').css('position') != 'relative') {
          $('.scroller_anchor').css('height', '0px');

          $('.scroller').css({
              'background': '#FFF',
              'position': 'relative'
          });
      }
  });
  $(function() {
    $( '#searchField' ).on("change keyup paste", function() {
      var isnum = /^\d+$/.test($( this ).val());
      if (isnum || $( this ).val() == '') {
        $( "#YearLabel" ).show();
        if ($( "#YearLabel" ).css('width') != '80px') {
          $( "#YearLabel" ).animate({
            opacity: 1,
            width: 80
            }, 100, function() {
          });
        }
      } else {
        if ($( "#YearLabel" ).css('width') != '1px') {
          $( "#YearLabel" ).animate({
            opacity: 0.25,
            width: 1
            }, 100, function() {
              $( "#YearLabel" ).hide();
          });
        }
      }
    });
  });
  </script>
</head>
<body>
  <?php
    ini_set('display_errors', 'On');
    error_reporting(E_ALL);

    $time = microtime();
    $time = explode(' ', $time);
    $time = $time[1] + $time[0];
    $start = $time;
    
    $mysqli = new mysqli("localhost", "searchQueries", "superhardpassword", "SciSearcher");
    /* check connection */
    if (mysqli_connect_errno()) {
        echo "Connect failed: ".mysqli_connect_error();
    }

    if(isset($_GET["page"])) {
      $limit = strval(($_GET["page"]*10)-10).', '.strval($_GET["page"]*10).';';
    } else {
      $limit = '0, 10;';
    }

    $searchTerm = $_GET["search"];
    
    $selectString = "SELECT SQL_CALC_FOUND_ROWS urls.url, papers.title, papers.year, GROUP_CONCAT(authors.name), papers.abstract, papers.venueType, papers.venue, MATCH(authors.name) AGAINST(? IN BOOLEAN MODE) AS score FROM papers INNER JOIN authors ON papers.id=authors.paperid LEFT JOIN urls ON papers.id=urls.paperid ";
    $keywordSelectString = "SELECT SQL_CALC_FOUND_ROWS urls.url, papers.title, papers.year, GROUP_CONCAT(authors.name), papers.abstract, papers.venueType, papers.venue, MATCH(papers.title) AGAINST(? IN BOOLEAN MODE) AS score  FROM papers INNER JOIN authors ON papers.id=authors.paperid LEFT JOIN urls ON papers.id=urls.paperid INNER JOIN keywords ON papers.id=keywords.paperid ";
    $endString =  " GROUP BY papers.title ORDER BY score DESC LIMIT ".$limit;

    if(isset($_GET["searchType"])) {
      $searchType = $_GET["searchType"];

      switch ($searchType){
        case "paper":
          $query = $keywordSelectString."WHERE MATCH(papers.title, keywords.keyword) AGAINST(? IN BOOLEAN MODE)".$endString;
          $stmt = $mysqli->prepare($query);
      break;
        case "year" :
          $stmt = $mysqli->prepare("SELECT SQL_CALC_FOUND_ROWS urls.url, papers.title, papers.year, GROUP_CONCAT(authors.name), papers.abstract, papers.venueType, papers.venue, 1 AS score FROM papers, authors, urls WHERE papers.id=authors.paperid AND papers.id=urls.paperid AND papers.year = ? group by papers.title LIMIT 10;");      
          break;
        case "author" :
          $query = $selectString."WHERE MATCH(authors.name) AGAINST(? IN BOOLEAN MODE)".$endString;
          $stmt = $mysqli->prepare($query);
          break;
      }
    } else {
      if (preg_match("/^[0-9]{4}$/", $searchTerm, $matches)) {
        $stmt = $mysqli->prepare("SELECT SQL_CALC_FOUND_ROWS urls.url, papers.title, papers.year, GROUP_CONCAT(authors.name), papers.abstract, papers.venueType, papers.venue, 1 AS score FROM papers, authors, urls WHERE papers.id=authors.paperid AND papers.id=urls.paperid AND papers.year = ? group by papers.title LIMIT 10;");
        $searchType = 'year';
      } else {
        $query = $keywordSelectString."WHERE MATCH(papers.title, keywords.keyword) AGAINST(? IN BOOLEAN MODE)".$endString;
        $stmt = $mysqli->prepare($query);
        $searchType = 'paper';
      }
    }
  
    if ($searchType == 'year') {
      if ($stmt) {
      $stmt->bind_param("i", $searchTerm);
    }
  } else {
    if ($stmt) {
      $stmt->bind_param("ss", $searchTerm, $searchTerm);
    }
  }

  $stmtSize = $mysqli->prepare("SELECT FOUND_ROWS();");

  ?>

  <header>
    <h2 class="pageTitle1">Project</h2>
    <h1 class="pageTitle2">SciSearcher</h4> 

    <div class="scroller_anchor"></div>
    <form action="results.php" method="GET" class="scroller">
      <input type="text" name="search" class="round" id='searchField' value="<?php echo $searchTerm ?>"/>
      <button type="submit" class="button">Search</button>
    <div class="searchOptions">

      <input type="radio" name="searchType" id="Paper" value='paper' onchange='this.form.submit()'
      <?php 
        if($searchType == 'paper') {
          echo "checked";
        }
      ?>
      />
        <label for="Paper">Paper</label>


      <input type="radio" name="searchType" id="Author" value='author' onchange='this.form.submit()'
      <?php 
        if($searchType == 'author') {
          echo "checked";
        }
      ?>
      />
        <label for="Author">Author</label>


      <input type="radio" name="searchType" id="Year" value='year' onchange='this.form.submit()'
      <?php 
        if($searchType == 'year') {
          echo "checked";
        }
      ?>
      />
        <label for="Year" id='YearLabel'>Year</label>

  <?php
    $resultsHTML = "<table class='results'>";
    $numRows = 0;
    $stmt->execute();
    if ($stmt->bind_result($link, $title, $year, $author, $abstract, $venueType, $venue, $score)) {
        while ($stmt->fetch()) {
            $resultsHTML .= "<tr>";
        $resultsHTML .= "<td  class='result'>";
        if (isset($link)) {
              $resultsHTML .= "<a class='title' href='".$link."'' target='_blank'>".$title."</a>";
        } else {
              $resultsHTML .= "<a class='title'>".$title."</a>";
        }
            $resultsHTML .= "<div class='resultInfo'>";
            $resultsHTML .= "<p class='author'>By ".$author.", ".$year."</p>";
            $resultsHTML .= "</div>";
            if ($abstract == ' ') {
              $resultsHTML .= "<p class='abstract'><span class='placeholder'>No abstract available.</span></p>";
            }else {
              $resultsHTML .= "<p class='abstract'>".$abstract."</p>";
            }
            if($venueType != '') {
              $resultsHTML .= "<p class='venueDetails'><span id='venueType'>".$venueType.":</span> ".$venue."</p>";
          }
            $resultsHTML .= "</td>";
      $resultsHTML .= "</tr>";
            $numRows++;
        }

        if ($numRows <= 0) {
          $resultsHTML .= "<tr><td><p class='abstract'><span class='placeholder'>No results found.</span></p></td></tr>";
        }     
    }

    $stmt->close();
       
    $resultsHTML .= "</table>";

    $time = microtime();
    $time = explode(' ', $time);
    $time = $time[1] + $time[0];
    $finish = $time;
    $total_time = round(($finish - $start), 4);

    $stmtSize->execute();
    $stmtSize->bind_result($size);
    $stmtSize->fetch();
    echo "<div id='count'>".$size." matches (".$total_time." seconds)</div>";
    //echo "<div id='count'>(".$total_time." seconds)</div>";
    $stmtSize->close();
    $mysqli->close();
    echo "</div>";
    echo "</form>";
    echo "</header>";
    echo $resultsHTML;   
  ?>

  <div class='pageNumbers'>
    <?php

      $numPages = $size/10;
      if(!isset($_GET["page"])){
        $_GET["page"] = 1;
      }
      for($i=1; ($size - ($i-1)*10) > 0; $i++) {
        if ($i > $numPages || $i == 1 || $i == $_GET["page"] || $i == $_GET["page"]-1 || $i == $_GET["page"] +1 || $i == $_GET["page"]-2 || $i == $_GET["page"] +2) {
            echo "<a ";
            if(($i==1 && !isset($_GET["page"])) || (isset($_GET["page"]) && $_GET["page"] == $i)) {
              echo "class='selected' >".$i."</a>";
            } else {
            echo "href='http://sproj09.cs.nott.ac.uk/results.php?search=".$_GET["search"]."&searchType=".$_GET["searchType"]."&page=".$i
            ."''>".$i."</a>";
        }
        } elseif ( $i == $_GET["page"]-3 || $i == $_GET["page"] +3 ) {
          echo "<span class='truncs'>...</span>";
        }
        
      }
    ?>
  </div>
</body>
<footer id='resultFooter'>
  <div>
    A study of bibliographic data in the research community, by bxs02u, yxc02u, yxm03u, nxm02u, zxp03u, tsc03u.
  </div>
</footer>
</html>
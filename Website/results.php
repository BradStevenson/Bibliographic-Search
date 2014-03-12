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
       
      if ($(this).scrollTop() >= scroller_anchor && $('.scroller').css('position') != 'fixed')
          $('.scroller').css({
              'background': 'rgba(255,255,255,0.95)',
              'position': 'fixed',
              'top': '0px'
          });
          $('.scroller_anchor').css('height', '50px');
      }
      else if ($(this).scrollTop() < scroller_anchor && $('.scroller').css('position') != 'relative')
          $('.scroller_anchor').css('height', '0px');
        
          $('.scroller').css({
              'background': '#FFF',
              'position': 'relative'
          });
      }
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
    
    $mysqli = new mysqli("localhost", "root", "mNNhv13uBB", "SciSearcher");
    /* check connection */
    if (mysqli_connect_errno()) {
        echo "Connect failed: ".mysqli_connect_error();
    }

    $searchTerm = $_GET["search"];
    
    if(isset($_GET["searchType"])){
      $searchType = $_GET["searchType"];

      switch ($searchType){
        case "keyword":
          $query = "SELECT urls.url, papers.title, papers.year, GROUP_CONCAT(authors.name), papers.abstract FROM papers, authors, urls, keywords WHERE papers.id=authors.paperid AND papers.id=urls.paperid AND papers.id=keywords.paperid AND keywords.keyword LIKE '%".$searchTerm."%' group by papers.title LIMIT 10;";
          $querySize = "SELECT COUNT(*) FROM keywords WHERE keyword LIKE '%".$searchTerm."%'";
          break;
        case "title":
          $query = "SELECT urls.url, papers.title, papers.year, GROUP_CONCAT(authors.name), papers.abstract FROM papers, authors, urls WHERE papers.id=authors.paperid AND papers.id=urls.paperid AND papers.title LIKE '%".$searchTerm."%' group by papers.title LIMIT 10;";
          $querySize = "SELECT COUNT(*) FROM papers WHERE title LIKE '%".$searchTerm."%'";
          break;
        case "year" :
          $query = "SELECT urls.url, papers.title, papers.year, GROUP_CONCAT(authors.name), papers.abstract FROM papers, authors, urls WHERE papers.id=authors.paperid AND papers.id=urls.paperid AND papers.year = '".$searchTerm."' group by papers.title LIMIT 10;";      
          $querySize = "SELECT COUNT(*) FROM papers WHERE year = '".$searchTerm."'";
          break;
        case "author" :
          $query = "SELECT urls.url, papers.title, papers.year, GROUP_CONCAT(authors.name), papers.abstract FROM papers, authors, urls WHERE papers.id=authors.paperid AND papers.id=urls.paperid AND authors.name LIKE '%".$searchTerm."%'  group by papers.title LIMIT 10;";
          $querySize = "SELECT COUNT(*) FROM authors WHERE name LIKE '%".$searchTerm."%'";
          break;
        case "field" :
          $query = "SELECT urls.url, papers.title, papers.year, GROUP_CONCAT(authors.name), papers.abstract FROM papers, authors, urls WHERE papers.id=authors.paperid AND papers.id=urls.paperid AND field LIKE '%".$searchTerm."%' LIMIT 10;";
          $querySize = "SELECT COUNT(*) FROM papers WHERE field LIKE '%".$searchTerm."%'";
          break;
      }
    } else {
      if (preg_match("/^[0-9]{4}$/", $searchTerm, $matches)) {
        $query = "SELECT urls.url, papers.title, papers.year, GROUP_CONCAT(authors.name), papers.abstract FROM papers, authors, urls WHERE papers.id=authors.paperid AND papers.id=urls.paperid AND papers.year = '".$searchTerm."' group by papers.title LIMIT 10;";
        $querySize = "SELECT COUNT(*) FROM papers WHERE year = '%".$searchTerm."%'";
        $searchType = 'year';
      } else {
        $query = "SELECT urls.url, papers.title, papers.year, GROUP_CONCAT(authors.name), papers.abstract FROM papers, authors, urls, keywords WHERE papers.id=authors.paperid AND papers.id=urls.paperid AND papers.id=keywords.paperid AND keywords.keyword LIKE '%".$searchTerm."%' group by papers.title LIMIT 10;";
        $querySize = "SELECT COUNT(*) FROM keywords WHERE keyword LIKE '%".$searchTerm."%'";
        $searchType = 'keyword';
      }
    }
    ?>

  <header>
    <h2 class="pageTitle1">Project</h2>
    <h1 class="pageTitle2">SciSearcher</h4> 

    <div class="scroller_anchor"></div>
    <form action="results.php" method="GET" class="scroller">
      <input type="text" name="search" class="round" value="<?php echo $searchTerm ?>"/>
      <button type="submit" class="button">Search</button>
    <div class="searchOptions">
      <input type="radio" name="searchType" id="Keyword" value='keyword' onchange='this.form.submit()'
      <?php 
        if($searchType == 'keyword') {
          echo "checked";
        }
      ?>
      />
        <label for="Keyword">Keyword</label>
      <input type="radio" name="searchType" id="Title" value='title' onchange='this.form.submit()'
      <?php 
        if($searchType == 'title') {
          echo "checked";
        }
      ?>
      />
        <label for="Title">Title</label>
      <input type="radio" name="searchType" id="Year" value='year' onchange='this.form.submit()'
      <?php 
        if($searchType == 'year') {
          echo "checked";
        }
      ?>
      />
        <label for="Year">Year</label>
      <input type="radio" name="searchType" id="Author" value='author' onchange='this.form.submit()'
      <?php 
        if($searchType == 'author') {
          echo "checked";
        }
      ?>
      />
        <label for="Author">Author</label>
      <input type="radio" name="searchType" id="Field" value='field' onchange='this.form.submit()'
      <?php 
        if($searchType == 'field') {
          echo "checked";
        }
      ?>
      />
        <label for="Field">Field</label>


  <?php
    $resultsHTML = '';
		$resultsHTML .= "<table class='results'>";
    $numRows = 0;
    if ($result = $mysqli->query($query)) {
        while ($row = $result->fetch_row()) {
       	  	$resultsHTML .= "<tr>";
		      	$resultsHTML .= "<td  class='result'>";
          	$resultsHTML .= "<a class='title' href='".$row[0]."'>".$row[1]."</a>";
            $resultsHTML .= "<div class='resultInfo'>";
            $resultsHTML .= "<p class='author'>".$row[3]."</p>";
            $resultsHTML .= "<p class='year'>".$row[2]."</p>";
            $resultsHTML .= "</div>";
            if ($row[4] == ' ') {
              $resultsHTML .= "<p class='abstract'><span class='placeholder'>No abstract available.</span></p>";
            }else {
              $resultsHTML .= "<p class='abstract'>".$row[4]."</p>";
            }

          	$resultsHTML .= "</td>";
			      $resultsHTML .= "</tr>";
            $numRows++;
        }
            
        $result->close();
  	}
    $resultsHTML .= "</table>";

    $time = microtime();
    $time = explode(' ', $time);
    $time = $time[1] + $time[0];
    $finish = $time;
    $total_time = round(($finish - $start), 4);

    $sizeResult = $mysqli->query($querySize);
    $size = $sizeResult->fetch_row();
    echo "<div id='count'>".$numRows." matches of ".$size[0]." in ".$total_time." seconds</div>";
    $sizeResult->close();
    $mysqli->close();
    echo "</div>";
    echo "</form>";
    echo "</header>";
    echo $resultsHTML;   
	?>
        
</body>
</html>

<!DOCTYPE HTML>
<html>
<head>
  <title>Results</title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link href="css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" type="text/css" href="results-style.css">
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
    if (preg_match("/^[0-9]{4}$/", $searchTerm, $matches)) {
      $query = "SELECT papers.title, papers.year, GROUP_CONCAT(authors.name), papers.abstract FROM papers, authors WHERE papers.id=authors.paperid AND papers.year = '".$searchTerm."' group by papers.title LIMIT 10;";
    } else {
      $query = "SELECT * FROM papers WHERE title LIKE '%".$searchTerm."%' LIMIT 10;";
    }
    
    $searchTerm = $_GET["search"];
    // $searchOptions = $_GET["searchOptions"];
    
    
    // switch ($searchOptions){
    //   case "Title":
    //     $query = "SELECT * FROM papers ORDER BY pr WHERE title = '".$searchTerm."' LIMIT 10;";
    //     break;
    //   case "Year" :
    //     $query = "SELECT * FROM papers ORDER BY pr WHERE year = '".$searchTerm."'"; 
    //     break;
    //   case "Author" :
    //     $query =  "SELECT * FROM papers ORDER BY pr WHERE paper.id = SELECT authors.paperid FROM authors WHERE authors.name = '".$searchTerm."' LIMIT 10;";
    //     break;
    //   case "Field" :
    //     $query = "SELECT * FROM papers ORDER BY pr WHERE papers.field = '".$searchTerm."' LIMIT 10;";
    // }
    ?>

  <header>
    <h1 class="pageTitle">Project SciSearcher</h4>      
    <form action="results.php" method="GET">
      <input type="text" name="search" class="round" value="<?php echo $searchTerm ?>" required/>
      <button type="submit" class="button">Search</button>
    </form>
  </header>

  <?php
    $resultsHTML = '';
		$resultsHTML .= "<table class='results'>";
    $numRows = 0;
    if ($result = $mysqli->query($query)) {
        while ($row = $result->fetch_row()) {
       	  	$resultsHTML .= "<tr>";
		      	$resultsHTML .= "<td  class='result'>";
          	$resultsHTML .= "<h1 class='title'>".$row[0]."</h1>";
            $resultsHTML .= "<div class='resultInfo'>";
            $resultsHTML .= "<p class='author'>".$row[2]."</p>";
            $resultsHTML .= "<p class='year'>".$row[1]."</p>";
            $resultsHTML .= "</div>";
            if ($row[3] == ' ') {
              $resultsHTML .= "<p class='abstract'><span class='placeholder'>No abstract available.</span></p>";
            }else {
              $resultsHTML .= "<p class='abstract'>".$row[3]."</p>";
            }

          	$resultsHTML .= "</td>";
			      $resultsHTML .= "</tr>";
            $numRows++;
        }
            
        $result->close();
  	}
    $mysqli->close();
    $time = microtime();
    $time = explode(' ', $time);
    $time = $time[1] + $time[0];
    $finish = $time;
    $total_time = round(($finish - $start), 4);
	  $resultsHTML .= "</table>";
    echo "<div id='count'>".$numRows." matches in ".$total_time." seconds</div>";
    echo $resultsHTML;   
	?>
        
</body>
<script type="text/javascript" src="js/resultsScript.js"></script>
<script type="text/javascript" src="js/queries.js"></script>
<script type="text/javascript" src="jquery-1.8.3.min.js"></script>
<script type="text/javascript" src="js/bootstrap.js"></script>
</html>

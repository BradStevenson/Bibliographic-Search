<!DOCTYPE HTML>
<html>
<head>
  <title>Results</title>
  <link rel="stylesheet" type="text/css" href="results-style.css">
</head>
<body>
       <?php
    ini_set('display_errors', 'On');
    error_reporting(E_ALL | E_STRICT);

    $time = microtime();
    $time = explode(' ', $time);
    $time = $time[1] + $time[0];
    $start = $time;
    
    $mysqli = new mysqli("mysql.cs.nott.ac.uk", "gp13_pkl", "CVNFTW", "gp13_pkl");
    /* check connection */
    if (mysqli_connect_errno()) {
        echo "Connect failed: ".mysqli_connect_error();
    }

    $searchTerm = $_GET["search"];
    if (preg_match("/^[0-9]{4}$/", $searchTerm, $matches)) {
      $query = "SELECT * FROM Papers WHERE year = ".$searchTerm;
    } else {
      $query = "SELECT * FROM Papers WHERE title LIKE '%".$searchTerm."%'";
    }
        ?>
  <header>
    <h4 class="text2">Project SciSearcher</h4>      
    <form action="results.php" method="GET">
      <input type="text" name="search" class="round" placeholder="Enter search criteria here."/>
      <button type="submit" class="button">Search</button>
    </form>
  </header>
        <table class="results">
            <?php
        $numRows = 0;
                if ($result = $mysqli->query($query)) {
            while ($row = $result->fetch_row()) {
              echo "<tr>";
              echo "<div id='result'><span id='title'>".$row[1].
             "</span><span id='author'>".$row[2].
             "</span><span id='year'>".$row[3].
             "</span></div>";
              echo "</tr>";
              $numRows++;
            }
            /* free result set */
            $result->close();
        }
        $mysqli->close();
        $time = microtime();
        $time = explode(' ', $time);
        $time = $time[1] + $time[0];
        $finish = $time;
        $total_time = round(($finish - $start), 4);
        echo "<div id='count'>".$numRows." matches in ".$total_time." seconds</div>";
            ?>
        </table>
</body>
</html>
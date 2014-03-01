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
    
    // NEED TO MAKE A USER FOR QUERIES. CAN'T LEAVE AS ROOT.
    $mysqli = new mysqli("sproj08.cs.nott.ac.uk", "root", "mNNhv13uBB", "SciSearcher");
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
    
    
    
    /* Tiffany here  modify the line from 23 - 28*/
    
    $searchTerm = $_GET["search"];
    $searchOptions = $_GET["submit"];
    
    
    swtich ($searchOptions){
      case "Title":
        $query = "SELECT * FROM Papers ORDER BY pr WHERE title = '".$searchTerm."';";
        break;
      case "Year" :
        $query = "SELECT * FROM Papers ORDER BY pr WHERE year = '".$searchTerm."';"; 
        break;
      case "Author" :
        $query =  "SELECT * FROM Papers ORDER BY pr WHERE Paper.id = SELECT Authors.paperid FROM Authors WHERE Authors.name = '".$searchTerm."';";
        break;
      case "Field" :
        $query = "SELECT * FROM papers ORDER BY pr WHERE papers.field = '".$searchTerm."';";
    }
   
   
   
    
    /* if there is not relevant paper then show the wrong message to user*/
    if (mysqli_num_fields($query) == 0){}
    else{
      throw new Exception ("There is no relevant paper to entered search criteria");
    }
       
    
    try{}
    catch(Exception $e) {
      echo 'Message : '. $e ->getMessage();
    }
    
        ?>


  /** Tiffany modify here **/
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
              echo "<div id='result'><span class = "underline"><span id='title'>".$row[1].
             "</span><span id='author'>".$row[2].
             "</span><span id='year'>".$row[3].
             "</span><span id = 'field'".$row[4].
             "</span></div>";
              echo "</tr>";
              echo "<br>";
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

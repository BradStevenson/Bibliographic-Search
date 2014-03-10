<!DOCTYPE HTML>
<html>
<head>
  <title>Results</title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link href="css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" type="text/css" href="css/results-style.css">
</head>
<body>
       <?php
    ini_set('display_errors', 'On');
    error_reporting(E_ALL);

    $time = microtime();
    $time = explode(' ', $time);
    $time = $time[1] + $time[0];
    $start = $time;
    
    $mysqli = new mysqli("sproj09.cs.nott.ac.uk", "root", "mNNhv13uBB", "SciSearcher");
    /* check connection */
    if (mysqli_connect_errno()) {
        echo "Connect failed: ".mysqli_connect_error();
    }

    $searchTerm = $_GET["search"];
    if (preg_match("/^[0-9]{4}$/", $searchTerm, $matches)) {
      $query = "SELECT * FROM papers WHERE year = '".$searchTerm."' LIMIT 10;";
    } else {
      $query = "SELECT * FROM papers WHERE title LIKE '%".$searchTerm."%' LIMIT 10;";
    }
    
    $searchTerm = $_GET["search"];
    $searchOptions = $_GET["submit"];
    
    
    swtich ($searchOptions){
      case "Title":
        $query = "SELECT * FROM papers ORDER BY pr WHERE title = '".$searchTerm."' LIMIT 10;";
        break;
      case "Year" :
        $query = "SELECT * FROM papers ORDER BY pr WHERE year = '".$searchTerm."' LIMIT 10;"; 
        break;
      case "Author" :
        $query =  "SELECT * FROM papers ORDER BY pr WHERE paper.id = SELECT authors.paperid FROM authors WHERE authors.name = '".$searchTerm."' LIMIT 10;";
        break;
      case "Field" :
        $query = "SELECT * FROM papers ORDER BY pr WHERE papers.field = '".$searchTerm."' LIMIT 10;";
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
              echo "<tr class="result">";
              echo "<div><span id="title"><u><h4>Name of the article: </font></h4></u></span></div>".$row[1].
             "<div class="col-md-3"><span id="author">Author： ".$row[2]."</span></div>".
             "<div class="col-md-3"><span id="field">Year： ".$row[3]."</span></div>".
             "<div class="col-md-3"><span id="year">Field： ".$row[4]."</span></div>";
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
<script type="text/javascript" src="js/resultsScript.js"></script>
<script type="text/javascript" src="js/queries.js"></script>
<script type="text/javascript" src="jquery-1.8.3.min.js"></script>
<script type="text/javascript" src="js/bootstrap.js"></script>
</html>

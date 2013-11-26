<!DOCTYPE HTML>
<html>
<head>
	<title>Results</title>
</head>
<body>
       <?php

            $server = mysql_connect("mysql.cs.nott.ac.uk","gp13_pkl", "CVNFTW");
            $db =  mysql_select_db("gp13_pkl",$server);
	          $searchTerm = $_GET["search"];
            $query = mysql_query("select * from Papers WHERE year = ".$searchTerm);
        ?>
        <table class="striped">
            <tr class="header">
                <td>Title</td>
                <td>Author</td>
                <td>Year</td>
            </tr>
            <?php
               while ($row = mysql_fetch_array($query)) {
                   echo "<tr>";
                   echo "<td>".$row[title]."</td>";
                   echo "<td>".$row[author]."</td>";
                   echo "<td>".$row[year]."</td>";
                   echo "</tr>";
               }

            ?>
        </table>
</body>
</html>


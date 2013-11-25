var mysql      = require('mysql');
var connection = mysql.createConnection({
  host     : 'cs.nott.ac.uk',
  user     : 'gp13_pkl',
  password : 'CVSFTW',
  database : 'gp13_pkl',
});

connection.connect();

connection.query('SELECT COUNT(*) AS amount FROM Papers', function(err, rows, fields) {
  if (err) throw err;

  console.log('There are ' + rows[0].amount + ' papers.');
});

connection.end();
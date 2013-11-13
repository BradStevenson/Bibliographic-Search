int LevenshteinDistance(char s[1..m], char t[1..n])
{
  // for all i and j, d[i,j] will hold the Levenshtein distance between
  // the first i characters of s and the first j characters of t;
  // note that d has (m+1)*(n+1) values
  declare int d[0..m, 0..n]
 
  clear all elements in d // set each element to zero
 
  // source prefixes can be transformed into empty string by
  // dropping all characters
  for i from 1 to m
    {
      d[i, 0] := i
    }
 
  // target prefixes can be reached from empty source prefix
  // by inserting every characters
  for j from 1 to n
    {
      d[0, j] := j
    }
 
  for j from 1 to n
    {
      for i from 1 to m
        {
          if s[i] = t[j] then
            d[i, j] := d[i-1, j-1]       // no operation required
          else
            d[i, j] := minimum
                    (
                      d[i-1, j] + 1,  // a deletion
                      d[i, j-1] + 1,  // an insertion
                      d[i-1, j-1] + 1 // a substitution
                    )
        }
    }
 
  return d[m, n]
}

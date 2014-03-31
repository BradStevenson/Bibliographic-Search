Project SciSearcher
====================

Exploration of bibliographic data, through a centralised search engine.

Considering a collection of academic research papers as a DirectedGraph<G,V> where G = Papers and V = Citations.  
We then apply a version of PageRank adapted for the substitions of academic papers instead of web pages and citations instead of hyperlinks. Giving ourselves a value on which to rank the importance of papers. 
We can make the educated assumption that theirs is a higher probability a paper within a research field would cite a paper within the same field than one outside of it. Using this assumption we can figure that a highly connected subgraph will accurately represent a research field. Therefore we can use an algorithm such as MinimumCut repeately to break our graph down into HCS's. 
 
Using these two methods we now have a representation of a collection of academic research papers which we can sort according to importance and separate into research fields of varying size.

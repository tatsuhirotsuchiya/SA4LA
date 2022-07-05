# SA4LA
SA4LA is a tool that uses simulated annealing to obtain (\overline{1},t)-locating arrays. 

Combinatorial testing usually uses covering arrays as test suites. 
Locating arrays are basically an extention of covering arrays. 
An added value of locating arrays is that they can not only 
detect the existence of a faulty interaction but can identify it. 

# Usage 
`java -jar program.jar inputfile`

# Inputfile format
    [strength]
    [#parameters]
    [#numOfValues1 #numOfValues2 ... ]
## Example
    2
    10
    2 2 2 2 2 2 3 3 3 4

# Credit
The source code as of 14/3/2019 is written by Tatsuya Konishi.

# Citation

@article{KONISHI2020106346,
title = {Using simulated annealing for locating array construction},
journal = {Information and Software Technology},
volume = {126},
pages = {106346},
year = {2020},
issn = {0950-5849},
doi = {https://doi.org/10.1016/j.infsof.2020.106346},
url = {https://www.sciencedirect.com/science/article/pii/S0950584920301130},
author = {Tatsuya Konishi and Hideharu Kojima and Hiroyuki Nakagawa and Tatsuhiro Tsuchiya},
keywords = {Locating arrays, Combinatorial interaction testing, Software testing, Simulated annealing}
}
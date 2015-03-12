A tool to handle large tables.

**WARNING**: This is a BETA version

The **Inserm** **Workbench** is a tool developped at the Center for Polymorphism Studies (CEPH) to help people at handling large tables in a rich graphical environment.

The tool is currently available as a Java webstart application at http://anybody.cephb.fr/perso/lindenb/tinytools/workbench.php


## Screenshot ##
![http://data.tumblr.com/NngfN9gsDh4plsq2kzs75bZeo1_400.png](http://data.tumblr.com/NngfN9gsDh4plsq2kzs75bZeo1_400.png)

## Requirements ##

  * java 1.6
  * java web start

## Sources ##

the JAVA Sources are available on code.google.com at http://code.google.com/p/cephlib

## Implementation ##

**Workbench** use the Java berkeleyDB API ( http://www.oracle.com/technology/documentation/berkeley-db/je/index.html ) to create an index of each row in a table.
The table are temporary (they are discarded when the program exits) but can be made persistent and stored in user's home.

## Manual ##

On opening, the application will create a folder in `${HOME}/.inserm-workbench-db`. The folder will contain the data handled by the Java berkeleyDB API
A first internal frame appear and containing the current persistent datases (The table is empty at the first time the application is launched)


### Opening a Table ###
  * **from a local file**: menu "File" -> 'load Table' -> 'Load Table'. A Dialog 'Select' appears. Select the file you want to upload into the workbench. Adjust the delimiter (this is a regular expression). Tabulation is the default. Click 'OK'. The file is uploaded  as a temporary table into the workbench. GZipped files are OK.
  * **from a URL**: : menu "File" -> 'load Table' -> 'Load URL'. A Dialog 'Select' appears. Select the URL you want to upload into the workbench (e.g. http://ftp.hapmap.org/genotypes/latest/rs_strand/non-redundant/genotypes_chr1_CEU_r24_nr.b36.txt.gz ). Adjust the delimiter (this is a regular expression). Tabulation is the default. Click 'OK'. The file is uploaded  as a temporary table into the workbench.GZipped files are OK.
  * **from a Database** (at this time only localhost and UCSC are supported):  menu "File" -> 'load Table' -> 'Load SQL'. A Dialog 'Select' appears. Select the JDBC parameters and enter a mysql query in the top left pane. e.g:
```
select * from hg18.snp129
```
Click on 'test' to see the result and then on 'OK'. The result is uploaded  as a temporary table into the workbench.

### Saving a Table ###
Menu 'File' -> 'Export': the content of the file is saved as a tab-delimited file. This file will be g-zipped if its name ends with ".gz"

### Making a Table persistent ###
Menu 'File' -> 'Persist': this file is saved in the user's repository. It will be available the next time the user uses the workbench

### Adding the information about the SNPs ###
Menu 'File' -> 'Join SNP Info' The Workbench uses the information about the SNP from the table hg18.snp129 at ucsc. Select the column containing the name of the SNP: The information about each SNP is downloaded from UCSC. The Workbench may seem to be frozen while the data are downloaded

### Join tables ###
Menu 'File' -> 'Join Table': this function is used to join two table on one or more column. Select the two tables to be joined and for each table add the column to be joined (left pane) to the right pane. The Workbench may seem to be frozen while the data are processed.

### Filter by javascript /Grep ###
Menu 'File' -> 'Filter by javascript': this function is a 'grep/awk' based on javascript. Enter a javascript expression that will be evaluated for each row. This function should return a boolean value. A extra parameter **row** (an array of string) is passed to the program for each row. e.g.
```
row[0]=="chr6" && row[1]>100 && row[2]<200
```


### Uniq ###
Menu 'File' -> 'Uniq': find the unique values in the tables. Select the values to be grouped in the dialog and press 'OK'. A combo box allow you to find the data only duplicated, not duplicated, unique.


### Sort ###
Menu 'File' -> 'Sort': sorts the table with several column. Select the column to be sorted and press ok. At this time, only the alphanumeric sort is supported.

### Head ###
Menu 'File' -> 'Head': keep the **N** first rows in a table. Select the number of row to be keep and press ok.

### Tail ###
Menu 'File' -> 'Tail': keep the **N** last rows in a table. Select the number of row to be keep and press ok.

### Cut ###
Menu 'File' -> 'Cut': remove some columns from the table. Select the column to be keep and press ok.


### Adding a column with javascript ###
Menu 'File' -> 'New Column': this function add a new column to the table by evaluating a  javascript expression. Enter an expression that will be evaluated for each row. This function should return a value. A extra parameter **row** (an array of string) is passed to the program for each row. e.g.
```
row[0].substring(2)+".1"
```

### Concatenate Tables ###
Workbench: Menu 'Tables' -> 'Concat': this function two ore more tables. select the tables to be concatenated and press 'ok'.


### Finding Genomic Overlaps ###
Workbench: Menu 'Tables' -> 'Concat': this function takes two tables containing information about a genomic location and join the data if the position overlap. Each table should contains 3 columns about:
  * chromosome
  * start
  * end
In the dialog, select the tables to be joined and the columns corresponding to the chromosome/start/end and press 'ok'. The Workbench may seem to be frozen while the data are processed.

## TODO ##
What do you need ?

## Author ##
Pierre Lindenbaum Phd
Centre d'Etude du Polymorphisme Humain
Paris
plindenbaum at yahoo fr
http://plindenbaum.blogspot.com
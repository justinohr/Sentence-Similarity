# Environment: Windows 10, jdk-9.0.1
# This program uses Stanford Parser which is released on 2017-06-09.
# how to execute the code on command line interface:
    1. move working directory to src
    2. compile the code with javac -cp ejml-0.23.jar;stanford-parser.jar;stanford-parser-3.8.0-models.jar; -d . zhsh/*.java
    3. execute the code with java -cp ejml-0.23.jar;stanford-parser.jar;stanford-parser-3.8.0-models.jar; zhsh.Main edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz "NAME OF INPUT FILE"
# If you execute the code you will get the similarity of two sentences which are the first two sentences written on input file. Sentences are delimited by period.
# Also you will get all the sub-trees of parse trees deriven from the given sentences.

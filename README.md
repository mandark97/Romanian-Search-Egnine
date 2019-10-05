# Romanian Search Engine

Romanian Search engine for Information Retrival course 1st year Master

## Versions

- Java 1.8
- Lucene 8.0.0
- Tika 1.2.0

## Usage

On **Initialize.java** set STOPWORDS_FILE and INDEX_PATH to the correct paths. The paths will be used in both **Indexer** and **Searcher**.

On runtime the diacritics in the stopwords file are removed.

### Indexer

On **Indexer** class set DEFAULT_FILES_DIRECTORY constant to the path that contains the files to be indexed. The path of the index will be the one specified in **Initialize**.

### Searcher

Change the parameter on searchQuery method in order to search in the index. The index path is the one specified in the **Initialize** class.

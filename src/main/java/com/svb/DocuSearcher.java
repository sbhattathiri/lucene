package com.svb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

public class DocuSearcher {

    public static void main(String[] args) {
        try {
            indexDocuments();
            searchDocuments();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
    }

    private static void indexDocuments() throws IOException {
        StandardAnalyzer standardAnalyzer = new StandardAnalyzer();

        Directory index = MMapDirectory.open(Path.of("./index"));
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(standardAnalyzer);
        IndexWriter indexWriter = new IndexWriter(index, indexWriterConfig);

        try (Stream<Path> files = Files.list(Path.of("data"))) {
            files.filter(path -> path.toString().endsWith(".txt"))
                    .forEach(file -> {
                        try {
                            List<String> lines = Files.readAllLines(file);
                            for (String line : lines) {
                                Document doc = new Document();
                                doc.add(new TextField("filename", file.getFileName().toString(), Field.Store.YES));
                                doc.add(new TextField("content", line, Field.Store.YES));
                                indexWriter.addDocument(doc);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
        indexWriter.close();

        System.out.println("Indexed  documents");
    }

    private static void searchDocuments() throws IOException, ParseException {
        Directory index = MMapDirectory.open(Path.of("./index"));
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        StandardAnalyzer analyzer = new StandardAnalyzer();

        QueryParser parser = new QueryParser("content", analyzer);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter search query (or 'quit' to exit):");

        while (true) {
            System.out.print("> ");
            String queryStr = scanner.nextLine();

            if ("quit".equals(queryStr)) {
                break;
            }

            Query query = parser.parse(queryStr);
            TopDocs results = searcher.search(query, 10);

            System.out.println("Found " + results.totalHits + " results:");

            for (ScoreDoc scoreDoc : results.scoreDocs) {
                Document doc = searcher.storedFields().document(scoreDoc.doc);
                System.out.println("Score: " + scoreDoc.score +
                        " | Content: " + doc.get("content") +
                        " | File: " + doc.get("filename"));
            }
            System.out.println();
        }

        reader.close();
        scanner.close();
    }

}

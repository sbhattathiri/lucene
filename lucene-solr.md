# Lucene to Solr Progression Guide

Perfect! Solr is built on Lucene, so your knowledge transfers directly. Here's the progression:

## What you know (Lucene):
- **IndexWriter** - adds documents
- **IndexSearcher** - searches documents  
- **Document/Field** - data structure
- **QueryParser** - converts text to queries
- **Directory** - where index is stored

## Solr = Lucene + Web Interface + Configuration

## Step 1: Install Solr
```bash
brew install solr
solr start
```
Visit: http://localhost:8983/solr

## Step 2: Create a core (like your Lucene Directory)
```bash
solr create -c documents
```

## Step 3: Your Lucene concepts in Solr:
```java
// Lucene: Create document
Document doc = new Document();
doc.add(new TextField("content", "Apache Lucene", Field.Store.YES));
writer.addDocument(doc);

// Solr: Same thing via HTTP
curl -X POST "http://localhost:8983/solr/documents/update/json/docs" \
  -H "Content-Type: application/json" \
  -d '[{"content":"Apache Lucene"}]'
```

## Step 4: Your search becomes:
```bash
# Instead of QueryParser + IndexSearcher
curl "http://localhost:8983/solr/documents/select?q=content:lucene"
```

Solr wraps your Lucene knowledge in a web service.

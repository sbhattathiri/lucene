### Learning Apache-Lucene

```
docker build -t lucene .

docker run -it --name lucene lucene

docker run -it -v ./data:/app/data -v ./index:/app/index --name lucene lucene
```

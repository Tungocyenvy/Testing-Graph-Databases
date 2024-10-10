Kiểm thử cơ sở dữ liệu đồ thị sử dụng Predicate Partitioning (Phân vùng)
Bài báo gốc:  https://www.research collection.ethz.ch/bitstream/handle/20.500.11850/573006/1/Kamm_Matteo.pdf

#Run
1. Để bắt đầu chạy thì run file DeleteAllNode trước để xóa các node hiện đang có Neo4j
2. Đối với redisGraph không còn hỗ trợ java nên cài docker để chạy redisGraph cho thực nghiệm
docker run -p 6379:6379 -it --rm redislabs/redisgraph

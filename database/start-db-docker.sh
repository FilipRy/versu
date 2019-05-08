docker stop mysql phpmyadmin
docker rm mysql phpmyadmin
docker rmi mmysql
docker network rm db
docker build . -f Dockerfile-mysql -t mmysql
docker network create db
docker run -d --name mysql --network db -p 3306:3306 mmysql --default-authentication-plugin=mysql_native_password
docker run -d --name phpmyadmin --network db -e PMA_HOST=mysql -p 8080:80 phpmyadmin/phpmyadmin:4.7
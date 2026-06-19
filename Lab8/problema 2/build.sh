#!/bin/bash

# --- DEFINESTE AICI NUMELE FOLDERELOR TALE ---
DIR_MANAGER="MessageManagerMicroservice"
DIR_TEACHER="TeacherMicroservice"
DIR_STUDENT="StudentMicroservice"
DIR_HEARTBEAT="HeartbeatMicroservice"

echo "[INFO] Incepem construirea sistemului din foldere separate..."

# ==========================================
# 1. BUILD MESSAGE MANAGER
# ==========================================
echo "[INFO] === Navigam in $DIR_MANAGER ==="
cd $DIR_MANAGER
mvn clean package
docker build -t localhost:5000/message_manager_microservice:v1 .
docker push localhost:5000/message_manager_microservice:v1
cd .. # Ne intoarcem in folderul principal

# ==========================================
# 2. BUILD TEACHER
# ==========================================
echo "[INFO] === Navigam in $DIR_TEACHER ==="
cd $DIR_TEACHER
mvn clean package

echo "-> Generare baza de date Profesor..."
cat <<EOF > questions_database.txt
Cum face vaca?
muuuuuuuu
Ce fac studentii?
noi muncim noi nu gandim, socialismul construim
EOF

docker build -t localhost:5000/teacher_microservice:v1 .
docker push localhost:5000/teacher_microservice:v1
rm questions_database.txt
cd ..

# ==========================================
# 3. BUILD STUDENTI (toti 3 din acelasi folder Student)
# ==========================================
echo "[INFO] === Navigam in $DIR_STUDENT ==="
cd $DIR_STUDENT
mvn clean package

echo "-> Generare si Build Student 1..."
cat <<EOF > questions_database.txt
Unde se da al 3-lea razboi mondial?
Pe Facebook
Tineti post?
Jean Calvin si trixitus inversus
EOF
docker build -t localhost:5000/student_microservice:tip1 .
docker push localhost:5000/student_microservice:tip1

echo "-> Generare si Build Student 2..."
cat <<EOF > questions_database.txt
Care e sensul vietii?
42
Cat e ceasul?
Cat ti-e nasul
De ce a trecut gaina strada?
Ca sa faca un ou
EOF
docker build -t localhost:5000/student_microservice:tip2 .
docker push localhost:5000/student_microservice:tip2

echo "-> Generare si Build Student 3..."
cat <<EOF > questions_database.txt
De unde vin copiii?
De la barza
Cati neuroni are un om?
Multi
EOF
docker build -t localhost:5000/student_microservice:tip3 .
docker push localhost:5000/student_microservice:tip3

rm questions_database.txt
cd ..

echo "[INFO] === Navigam in $DIR_HEARTBEAT ==="
cd $DIR_HEARTBEAT
mvn clean package
docker build -t localhost:5000/heartbeat_microservice:v1 .
docker push localhost:5000/heartbeat_microservice:v1
cd .. 

echo "[SUCCES] Toate microserviciile au fost construite cu succes!"

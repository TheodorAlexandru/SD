#!/bin/bash

# --- Configurari nume foldere ---
DIR_MANAGER="MessageManagerMicroservice"
DIR_TEACHER="TeacherMicroservice"
DIR_STUDENT="StudentMicroservice"

echo "[INFO] Incepem procedura de resetare si deploy..."

# 1. Curatenie: Oprim si stergem containerele vechi daca exista
echo "[INFO] Curatam containerele vechi..."
docker rm -f message_manager teacher_microservice student_microservice_1 student_microservice_2 student_microservice_3 heartbeat_microservice 2>/dev/null

# 2. Reteaua: Ne asiguram ca reteaua ms-net exista
docker network ls | grep -q "ms-net" || docker network create ms-net

# 3. Pornire Message Manager
echo "[INFO] Pornim Message Manager..."
docker run -d -p 1500:1500 --name message_manager --network=ms-net localhost:5000/message_manager_microservice:v1

# 4. Pornire Teacher
echo "[INFO] Pornim Teacher pe portul 1600..."
docker run -d -p 1600:1600 -e MESSAGE_MANAGER_HOST='message_manager' --name teacher_microservice --network=ms-net localhost:5000/teacher_microservice:v1

# 5. Pornire Student 1
echo "[INFO] Pornim Student 1 pe portul 1701..."
docker run -d -p 1701:1701 -e STUDENT_PORT=1701 -e MESSAGE_MANAGER_HOST='message_manager' --name student_microservice_1 --network=ms-net localhost:5000/student_microservice:tip1

# 6. Pornire Student 2
echo "[INFO] Pornim Student 2 pe portul 1702..."
docker run -d -p 1702:1702 -e STUDENT_PORT=1702 -e MESSAGE_MANAGER_HOST='message_manager' --name student_microservice_2 --network=ms-net localhost:5000/student_microservice:tip2

# 7. Pornire Student 3
echo "[INFO] Pornim Student 3 pe portul 1703..."
docker run -d -p 1703:1703 -e STUDENT_PORT=1703 -e MESSAGE_MANAGER_HOST='message_manager' --name student_microservice_3 --network=ms-net localhost:5000/student_microservice:tip3

echo "[INFO] Pornim Heartbeat..."
docker run -d -e MESSAGE_MANAGER_HOST='message_manager' --name heartbeat_microservice --network=ms-net localhost:5000/heartbeat_microservice:v1

echo "[SUCCES] Toate containerele au fost resetate si repornite!"
echo "[INFO] Porturi active: 1500 (MessageManager), 1600 (Teacher), 1701-1703 (Studenti)"

release_tag="1.0-20261126"
echo "1. Build the React frontend to this Spring Boot app's static content directory"
npm --prefix ../frontend run build -- --outDir "$PWD/rest-api/src/main/resources/static" --emptyOutDir
echo "2. Build jar"
mvn clean package
echo -e "3. \U1F310 Upload to GCP e2-micro instance"
gcloud compute scp rest-api/target/rest-api-1.0-SNAPSHOT.jar instance-20261114-143613:app-${release_tag}.jar --project=project-7381b287-a578-46f1-b58 --zone=us-central1-c
echo -e "4. \U1F310 publish the app in e2-micro instance"
gcloud compute ssh instance-20261114-143613 --zone=us-central1-c --project=project-7381b287-a578-46f1-b58 \
  --command="sudo mv app-${release_tag}.jar /opt/app/app.jar && sudo systemctl restart app"

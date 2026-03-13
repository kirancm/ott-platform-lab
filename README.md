"# ott-platform-lab" 

//Setup
//portforwording
nohup kubectl port-forward service/content-service 8080:80 >/tmp/port-forward.log 2>&1 &

//docker
# Point Docker CLI to Minikube's Docker daemon
eval $(minikube -p minikube docker-env)

docker build -t ott-content-service:1.0 .

//postgreSql
psql -U postgres -d ott
/dt


//Helm
Add Bitnami Helm Repository
helm repo add bitnami https://charts.bitnami.com/bitnami
helm upgrade --install ott-platform ./ott-platform-helm

helm uninstall ott-platform -n default
helm install ott-platform ./





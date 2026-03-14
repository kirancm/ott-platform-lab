"# ott-platform-lab" 

//Setup
//portforwording
nohup kubectl port-forward -n ingress-nginx service/ingress-nginx-controller 9090:80 >/tmp/ingress-port-forward.log 2>&1 &

nohup kubectl port-forward service/content-service 8080:80 >/tmp/port-forward.log 2>&1 &

//docker
# Point Docker CLI to Minikube's Docker daemon
eval $(minikube -p minikube docker-env)

docker build -t ott-content-service:1.0 .

//kubectl
 kubectl rollout restart deployment/analytics-service


//postgreSql
psql -U postgres -d ott
/dt


//Helm
Add Bitnami Helm Repository
helm repo add bitnami https://charts.bitnami.com/bitnami
helm upgrade --install ott-platform ./ott-platform-helm

helm uninstall ott-platform -n default
helm install ott-platform ./

//Kubernets ingress
//Install NGINX Ingress Controller
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/cloud/deploy.yaml





"# ott-platform-lab"

//Setup

// Minikube
minikube status
minikube start

//portforwording
* nohup kubectl port-forward -n ingress-nginx service/ingress-nginx-controller 9090:80 >/tmp/ingress-port-forward.log 2>\&1 \&
* nohup kubectl port-forward service/content-service 8080:80 >/tmp/port-forward.log 2>\&1 \&
* nohup kubectl port-forward svc/ott-platform-grafana 3000:80 >/tmp/port-forward.log 2>\&1 \&
* kubectl port-forward pod/ott-platform-jaeger-query-66d98fcf68-bw4tp 16686:16686
* nohup kubectl port-forward svc/elasticsearch-master 9200:9200 >/tmp/port-forward.log 2>\&1 \&
* kubectl port-forward svc/elasticsearch-master 9200:9200 --address 0.0.0.0



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
helm repo add elastic https://helm.elastic.co
helm repo update
helm dependency update

helm upgrade --install ott-platform ./ott-platform-helm
helm uninstall ott-platform -n default
helm install ott-platform ./

//Kubernets ingress
//Install NGINX Ingress Controller
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/cloud/deploy.yaml

//ElasticSearch
kubectl exec -it elasticsearch-master-0 -- curl -k -u elastic:<password> https://localhost:9200

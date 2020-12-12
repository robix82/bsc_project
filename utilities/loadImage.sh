
image=robix82/usi.ch-hse:0.1
filename=hse.tar

docker rmi $image
docker load < $filename

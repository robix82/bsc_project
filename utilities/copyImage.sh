
server=root@www.robix-projects.org:hse
image=robix82/usi.ch-hse:0.1
filename=hse.tar

here=$(pwd)

cd ../hse
mvn -Pprod clean install
cd $here

docker save $image > $filename
docker rmi $(docker images --filter "dangling=true" -q --no-trunc)

scp $filename $server

rm *.tar


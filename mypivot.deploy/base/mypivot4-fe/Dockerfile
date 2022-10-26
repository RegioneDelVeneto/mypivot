FROM registry.access.redhat.com/ubi7/nodejs-12 as base
USER root

ARG USERNAME
ARG PASSWORD
ARG BRANCH

##RUN yum install -y git

RUN mkdir -p /mypivot4/snapshot
   
WORKDIR /app

RUN mv yourpivot4.sources mypivot4

WORKDIR /app/mypivot4/mypivot4-fe
	
RUN npm install -g @angular/cli@~10.0.1

RUN npm install -g typescript@~3.9.6

RUN npm install

RUN npm run build -- mypivot4-fe --base-href /mypivot4/snapshot/ --output-path /mypivot4/snapshot/mypivot4-fe --configuration mywire-snapshot


FROM registry.access.redhat.com/ubi8/openjdk-11 as server
USER root

RUN groupadd -g 9000 mypay4 \
 && chmod -R 777 /home/jboss \
 && adduser \
    --home "/mypivot4" \
    --gid 9000 \
    --no-create-home \
    --uid 9000 \
    mypay4

RUN microdnf install -y yum && \
    yum install -y nano && \
    yum install -y nmap && \
    yum install -y curl && \
    mkdir -p mypivot4 && \
    mkdir -p mypivot4/snapshot

###ADD mypivot4-fe /mypivot4/snapshot/mypivot4-fe
COPY --from=base --chown=mypay4:mypay4 /mypivot4 /mypivot4

ADD nginx-1.18.0-1.el8.ngx.x86_64.rpm /mypivot4/nginx-1.18.0-1.el8.ngx.x86_64.rpm
RUN rpm -iv /mypivot4/nginx-1.18.0-1.el8.ngx.x86_64.rpm

COPY ./cert/dhparam.pem          /mypivot4/ssl/certs/dhparam.pem
COPY ./cert/nginx-selfsigned.crt /mypivot4/ssl/certs/nginx-selfsigned.crt
COPY ./cert/nginx-selfsigned.key /mypivot4/ssl/private/nginx-selfsigned.key

RUN mkdir -p /var/log/nginx \
&&  mkdir -p /var/lib/nginx \ 
&&  chown 9000:9000 /var/log/nginx \
&&  chown 9000:9000 /var/lib/nginx \
&&  rm /etc/nginx/nginx.conf \
&&  rm -r /etc/nginx/conf.d \
&&  ln -sf /dev/stdout /var/log/nginx/access.log \
&&  ln -sf /dev/stderr /var/log/nginx/error.log \
&&  chown -R 9000:9000 /mypivot4 \
&&  chmod -R 777 /mypivot4 \
&&  chown -R 9000:9000 /var/cache/nginx \
&&  chmod -R g+w /var/cache/nginx \
&&  chown -R 9000:9000 /etc/nginx \
&&  chmod -R g+w /etc/nginx

WORKDIR /mypivot4
#USER mypay4

EXPOSE 8443

CMD ["nginx", "-g", "daemon off;"]
##CMD exec /bin/sh -c "trap : TERM INT; sleep 9999999999d & wait"




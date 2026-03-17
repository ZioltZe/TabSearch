FROM node:22 AS build
WORKDIR /app
COPY . .
RUN npm install
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

COPY certs/192.168.1.23+1.pem /etc/ssl/certs/192.168.1.23+1.pem
COPY certs/192.168.1.23+1-key.pem /etc/ssl/certs/192.168.1.23+1-key.pem

EXPOSE 443

FROM openjdk:11

# 设定时区
ENV TZ=Asia/Shanghai
RUN set -eux;\
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime;\
    echo $TZ > /etc/timezone

# 新建用户 jt1078
RUN set -eux;\
    addgroup --gid 1000 jt1078;\
    adduser --system --uid 1000 --gid 1000 --home=/home/jt1078 --shell=/bin/sh --disabled-password jt1078;\
    mkdir -p /home/jt1078/logs /home/jt1078/jt1078;\
    chown -R jt1078:jt1078 /home/jt1078

# 导入启动脚本
COPY --chown=jt1078:jt1078 docker-entrypoint.sh /home/jt1078/docker-entrypoint.sh

# 导入代码
COPY --chown=jt1078:jt1078 build/libs/JT1078MediaServer-0.2.0.jar /home/jt1078/jt1078/jt1078.jar

RUN ["chmod", "+x", "/home/jt1078/docker-entrypoint.sh"]

USER jt1078

ENTRYPOINT ["/home/jt1078/docker-entrypoint.sh"]

EXPOSE 10003 10004
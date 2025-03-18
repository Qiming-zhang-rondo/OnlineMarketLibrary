#!/bin/bash

# 服务名称和路径列表（用空格分隔）
services=(
    "cart-service:/Users/qiming/kafka/kafka-ecommerce-parent/cart-service/target/cart-service-1.0-SNAPSHOT.jar"
    "customer-service:/Users/qiming/kafka/kafka-ecommerce-parent/customer-service/target/customer-service-1.0-SNAPSHOT.jar"
    "order-service:/Users/qiming/kafka/kafka-ecommerce-parent/order-service/target/order-service-1.0-SNAPSHOT.jar"
    "payment-provider-service:/Users/qiming/kafka/kafka-ecommerce-parent/paymentProvider-service/target/paymentProvider-service-1.0-SNAPSHOT.jar"
    "payment-service:/Users/qiming/kafka/kafka-ecommerce-parent/payment-service/target/payment-service-1.0-SNAPSHOT.jar"
    "product-service:/Users/qiming/kafka/kafka-ecommerce-parent/product-service/target/product-service-1.0-SNAPSHOT.jar"
    "seller-service:/Users/qiming/kafka/kafka-ecommerce-parent/seller-service/target/seller-service-1.0-SNAPSHOT.jar"
    "shipment-service:/Users/qiming/kafka/kafka-ecommerce-parent/shipment-service/target/shipment-service-1.0-SNAPSHOT.jar"
    "stock-service:/Users/qiming/kafka/kafka-ecommerce-parent/stock-service/target/stock-service-1.0-SNAPSHOT.jar"
)

# 定义日志目录
LOG_DIR="./logs"
mkdir -p "$LOG_DIR"

# 启动所有微服务
echo "Starting all microservices..."

for service_entry in "${services[@]}"; do
    # 分割服务名称和 JAR 文件路径
    service_name=$(echo "$service_entry" | cut -d':' -f1)
    jar_path=$(echo "$service_entry" | cut -d':' -f2)
    log_file="$LOG_DIR/$service_name.log"

    # 检查 JAR 文件是否存在
    if [[ -f "$jar_path" ]]; then
        echo "Starting $service_name..."
        nohup java -jar "$jar_path" > "$log_file" 2>&1 &
        echo "$service_name started, logging to $log_file"
    else
        echo "JAR file for $service_name not found: $jar_path"
    fi
done

echo "All microservices started."

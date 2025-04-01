package com.example.shipment;

import com.example.common.entities.PackageStatus;
import com.example.common.entities.ShipmentStatus;
import com.example.shipment.model.PackageId;
import com.example.shipment.model.Shipment;
import com.example.shipment.model.Package;
import com.example.shipment.model.ShipmentId;
import com.example.shipment.repository.ShipmentRepository;
import com.example.shipment.repository.PackageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ShipmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private PackageRepository packageRepository;

    @BeforeEach
    public void setUp() {
        shipmentRepository.deleteAll();
    }

    @Test
    public void testUpdateShipment() throws Exception {
        // 创建并保存一个 Shipment 对象，带有嵌入的复合主键
        Shipment shipment = new Shipment();
        ShipmentId shipmentId = new ShipmentId(1, 1001);
        shipment.setId(shipmentId);
        shipment.setPackageCount(5);
        shipment.setTotalFreightValue(150.0f);
        shipment.setRequestDate(LocalDateTime.now());
        shipment.setStatus(ShipmentStatus.APPROVED);
        shipment.setFirstName("John");
        shipment.setLastName("Doe");
        shipment.setStreet("123 Main St");
        shipment.setZipCode("12345");
        shipment.setCity("New York");
        shipment.setState("NY");

        shipmentRepository.save(shipment);

        Package package1 = new Package();
        PackageId packageId1 = new PackageId(1, 1001, 1);
        package1.setId(packageId1);
        package1.setSellerId(123); // 示例sellerId
        package1.setProductId(456); // 示例productId
        package1.setProductName("Product A");
        package1.setFreightValue(50.0f);
        package1.setShippingDate(LocalDateTime.now());
        package1.setQuantity(10);
        package1.setStatus(PackageStatus.SHIPPED);
        packageRepository.save(package1);

        // 定义一个用于测试的唯一 instanceId
        String instanceId = "testInstance123";

        // 发送 PATCH 请求到 update 端点并验证响应
        mockMvc.perform(patch("/shipment/{instanceId}", instanceId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());

        // 验证数据库中的发货状态和相关字段是否已更新
        Optional<Shipment> shipmentOpt = shipmentRepository.findById(shipmentId);
        assert shipmentOpt.isPresent();
        Shipment updatedShipment = shipmentOpt.get();
        assert updatedShipment.getStatus() == ShipmentStatus.DELIVERY_IN_PROGRESS
                || updatedShipment.getStatus() == ShipmentStatus.CONCLUDED;
    }

    @Test
    public void testGetShipment() throws Exception {
        // 创建并保存一个 Shipment 对象，使用嵌入的复合主键
        Shipment shipment = new Shipment();
        ShipmentId shipmentId = new ShipmentId(1, 1001); // 使用嵌入式复合主键
        shipment.setId(shipmentId);
        shipment.setPackageCount(5); // 设置包裹数量
        shipment.setTotalFreightValue(150.0f); // 设置总运费
        shipment.setRequestDate(LocalDateTime.now()); // 设置请求日期
        shipment.setStatus(ShipmentStatus.APPROVED); // 设置状态
        shipment.setFirstName("John");
        shipment.setLastName("Doe");
        shipment.setStreet("123 Main St");
        shipment.setZipCode("12345");
        shipment.setCity("New York");
        shipment.setState("NY");

        // 保存到数据库
        shipmentRepository.save(shipment);

        // 通过 API 获取并验证结果
        mockMvc.perform(get("/shipment/1/1001")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.city").value("New York"));
    }
}

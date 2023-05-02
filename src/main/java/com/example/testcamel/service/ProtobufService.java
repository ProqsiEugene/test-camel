package com.example.testcamel.service;

import com.example.testcamel.dto.TrainDTO;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.runtime.RuntimeSchema;
import org.apache.camel.Exchange;

import org.springframework.stereotype.Component;

@Component
public class ProtobufService {

    public byte[] convertDtoToProtobuf(Exchange exchange) {

        TrainDTO trainDto = exchange.getIn().getBody(TrainDTO.class);
        // Создаем экземпляр схемы для DTO
        RuntimeSchema<TrainDTO> schema = RuntimeSchema.createFrom(TrainDTO.class);
        // Создаем буфер
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            // Преобразуем DTO в protobuf и записываем результат в буфер
            byte[] protobuff = ProtobufIOUtil.toByteArray(trainDto, schema, buffer);
//            System.out.println("Protobuf: " + Arrays.toString(protobuff));
            return protobuff;
        } finally {
            // очищаем буфер
            buffer.clear();
        }
    }
}

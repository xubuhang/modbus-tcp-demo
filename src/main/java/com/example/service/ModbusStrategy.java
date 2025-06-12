package com.example.service;
/**
 * Modbus TCP 统一操作接口
 * 支持读写单个寄存器
 */
public interface ModbusStrategy {
    /**
     * 读取保持寄存器（Holding Register）
     * @param ip       目标设备IP
     * @param port     端口号
     * @param slaveId  从站地址
     * @param address  寄存器地址
     * @return         读取到的寄存器值
     * @throws Exception  连接或通讯异常
     */
    int readRegister(String ip, int port, int slaveId, int address) throws Exception;

    /**
     * 写单个保持寄存器（Holding Register）
     * @param ip       目标设备IP
     * @param port     端口号
     * @param slaveId  从站地址
     * @param address  寄存器地址
     * @param value    要写入的值
     * @throws Exception  连接或通讯异常
     */
    void writeRegister(String ip, int port, int slaveId, int address, int value) throws Exception;
}

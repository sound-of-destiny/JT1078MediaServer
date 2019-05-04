package protocol;

import lombok.Data;

@Data
public class RTPMessage {
    // 标志位，确定是否是完整数据帧边界
    private byte M;
    // 负载类型
    private byte PT;
    // 包序号
    private int packageFlowId;
    // 终端设备SIM卡号 6byte
    private String SIM;
    // 逻辑通道号
    private int logicChannel;
    // 数据类型
    // 0000:视频I帧
    // 0001:视频P帧
    // 0010:视频B帧
    // 0011:音频帧
    // 0100:透传数据
    private byte dataType;
    // 分包处理标记
    // 0000:原子包，不可被拆分
    // 0001:分包处理时的第一个包
    // 0010:分包处理时的最后一个包
    // 0011:分包处理时的中间包
    private byte packageFlag;
    // 时间戳 mill
    private long timeStamp;
    // 该帧与上一个关键帧之间的时间间隔 mill
    private short lastIFrameInterval;
    // 该帧与上一帧之间的时间间隔 mill
    private short lastFrameInterval;
    // 后续数据体长度
    private short dataBodyLength;
    // 长度不超过 950 bytes
    private byte[] dataBody;
}

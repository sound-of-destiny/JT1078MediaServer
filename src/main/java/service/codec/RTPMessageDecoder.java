package service.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import protocol.RTPMessage;
import util.BCD8421Operater;

import java.util.List;

public class RTPMessageDecoder extends ByteToMessageDecoder {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("active");
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        if (in == null)
            return;

        in.markReaderIndex();
        //跳过无关紧要的数据
        in.skipBytes(1);

        RTPMessage msg = new RTPMessage();
        //M（1 bit）、PT（7 bit） 共占用 1 个字节
        byte b = in.readByte();

        msg.setM((byte)((b >> 7) & 0x1));
        msg.setPT((byte)(b & 0x7f));

        msg.setPackageFlowId(in.readShort());
        byte[] simNum = new byte[6];
        in.readBytes(simNum);
        msg.setSIM(BCD8421Operater.bcd2String(simNum));

        msg.setLogicChannel(in.readByte());

        //数据类型（4 bit）、分包处理标记（4 bit）共占用一个字节
        b = in.readByte();

        msg.setDataType((byte) (b >> 4));
        msg.setPackageFlag((byte) (b & 0x0f));

        if (msg.getDataType() != 4) {   //不为透传数据类型
            msg.setTimeStamp(in.readLong());
        }

        if (msg.getDataType() != 3 && msg.getDataType() != 4) { //视频数据类型才有以下字段
            msg.setLastIFrameInterval(in.readShort());
            msg.setLastFrameInterval(in.readShort());
        }

        //数据体长度
        msg.setDataBodyLength(in.readShort());

        if (in.readableBytes() < msg.getDataBodyLength()) {
            in.resetReaderIndex();
            return;
        }
        //数据体
        byte[] body = new byte[msg.getDataBodyLength()];
        in.readBytes(body);
        msg.setDataBody(body);
        out.add(msg);
    }
}

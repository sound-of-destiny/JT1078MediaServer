public class Test {

    /*AVCodec codec = avcodec.avcodec_find_encoder(avcodec.AV_CODEC_ID_AAC);
        AVCodecContext c = avcodec_alloc_context3(codec);
        c.bit_rate(64000);
        c.sample_fmt(AV_SAMPLE_FMT_FLTP);
        c.sample_rate(16000);
        c.channels(1);
        c.channel_layout(av_get_default_channel_layout(1));
        AVDictionary options = new AVDictionary(null);
        // Enable multithreading when available
        //c.thread_count(0);
        avcodec.avcodec_open2(c, codec, options);
        av_dict_free(options);
        int audio_outbuf_size = 256 * 1024;
        BytePointer audio_outbuf = new BytePointer(av_malloc(audio_outbuf_size));
        AVPacket avPacket = new AVPacket();
        av_init_packet(avPacket);
        avPacket.data(audio_outbuf);
        avPacket.size(audio_outbuf_size);
        AVFrame avFrame = av_frame_alloc();
        avFrame.nb_samples(c.frame_size());
        avFrame.format(c.sample_fmt());*/
    // test
                    /*ShortBuffer b = (ShortBuffer)audioFrame.samples[0];
                    Pointer p = new ShortPointer(b);

                    byte[] data = new byte[audioFrame.samples[0].capacity() * 2];
                    ByteBuffer buffer = ByteBuffer.allocate(audioFrame.samples[0].capacity() * 2);
                    buffer.asShortBuffer().put((ShortBuffer) audioFrame.samples[0]);
                    buffer.get(data);
                    log.info("data {}, data length {}", data, data.length);
                    avcodec_fill_audio_frame(avFrame, c.channels(), c.sample_fmt(), buffer, buffer.position(), 0);

                    //avFrame.data(0, buffer.position(0));
                    avFrame.linesize(0, buffer.position());

                    avFrame.quality(c.global_quality());
                    avFrame.data(0, new BytePointer(data).position(0));

                    avcodec_send_frame(c, avFrame);
                    avcodec_receive_packet(c, avPacket);
                    byte[] outData = new byte[avPacket.asByteBuffer().limit()];
                    log.info("psize : {}", avPacket);
                    byte[] outData = avPacket.size();
                    log.info("{}", outData);
                    log.info("{}", outData.length);*/



            /*short[] samples = new short[40];
            ByteBuffer.wrap(msg.getDataBody()).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(samples);
            ByteBuffer bb = ByteBuffer.allocate(samples.length * 2);
            bb.asShortBuffer().put(samples);
            ByteBuffer byteBuffer = ByteBuffer.wrap(bb.array());
            FileOutputStream fos = new FileOutputStream("audio.g726", true);
            FileOutputStream fos2 = new FileOutputStream("audio2.g726", true);
            FileChannel channel2 = fos2.getChannel();
            channel2.write(ByteBuffer.wrap(msg.getDataBody()));
            channel2.close();
            fos2.close();
            FileChannel channel = fos.getChannel();
            channel.write(byteBuffer);
            channel.close();
            fos.close();*/


            /*AVCodec codec = null;
            AVCodecContext avCodecContext = null;
            AVCodecParserContext avCodecParserContext = null;
            AVPacket avPacket = null;
            AVFrame avFrame = null;

            avPacket = avcodec.av_packet_alloc();
            codec = avcodec.avcodec_find_decoder(avcodec.AV_CODEC_ID_ADPCM_G726);
            avCodecParserContext = avcodec.av_parser_init(codec.id());
            avCodecContext = avcodec.avcodec_alloc_context3(codec);
            AVDictionary avDictionary = new AVDictionary();
            avcodec.avcodec_open2(avCodecContext, codec, avDictionary);
            avcodec.avcodec_send_packet(avCodecContext, avPacket);
            avcodec.avcodec_receive_frame(avCodecContext, avFrame);*/

}

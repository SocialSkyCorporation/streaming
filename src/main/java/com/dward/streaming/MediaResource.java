package com.dward.streaming;

import com.codahale.metrics.annotation.Timed;
import com.sun.jersey.spi.resource.Singleton;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Date;
@Path("/")
@Singleton
public class MediaResource {

    private final int CHUNK_SIZE = 1024 * 1024; // 1MB chunks
    private final MediaCache mediaCache = new MediaCache();

    @GET
    @Produces("text/html")
    @Timed
    public IndexView getIndex() {
        return new IndexView();
    }

    @GET
    @Timed
    @Path("{fileName}")
    public Response stream(@HeaderParam("Range") String range, @PathParam("fileName") String fileName) throws Exception {
        return buildStream(mediaCache.getFile(fileName), range);
    }

    /**
     * Adapted from http://stackoverflow.com/questions/12768812/video-streaming-to-ipad-does-not-work-with-tapestry5/12829541#12829541
     * @param asset Media file
     * @param range range header
     * @return Streaming output
     * @throws Exception IOException if an error occurs in streaming.
     */
    private Response buildStream(final File asset, final String range) throws Exception {
        // range not requested : Firefox, Opera, IE do not send range headers
        final String mediaType = VideoMediaTypes.parse(asset.getName());
        if (range == null) {
            StreamingOutput streamer = getStreamingOutput(asset);
            return Response.ok(streamer)
                    .status(200)
                    .header(HttpHeaders.CONTENT_LENGTH, asset.length())
                    .type(mediaType)
                    .build();
        }

        String[] ranges = range.split("=")[1].split("-");
        final int from = Integer.parseInt(ranges[0]);
        /**
         * Chunk media if the range upper bound is unspecified. Chrome sends "bytes=0-"
         */
        int to = CHUNK_SIZE + from;
        if (to >= asset.length()) {
            to = (int) (asset.length() - 1);
        }
        if (ranges.length == 2) {
            to = Integer.parseInt(ranges[1]);
        }

        final String responseRange = String.format("bytes %d-%d/%d", from, to, asset.length());
        final RandomAccessFile raf = new RandomAccessFile(asset, "r");
        raf.seek(from);

        final int len = to - from + 1;
        final MediaStreamer streamer = new MediaStreamer(len, raf);
        return Response
                .ok(streamer)
                .status(206)
                .header("Accept-Ranges", "bytes")
                .header("Content-Range", responseRange)
                .header(HttpHeaders.CONTENT_LENGTH, streamer.getLenth())
                .header(HttpHeaders.LAST_MODIFIED, new Date(asset.lastModified()))
                .type(mediaType)
                .build();
    }

    private StreamingOutput getStreamingOutput(final File asset) {
        return new StreamingOutput() {
            @Override
            public void write(final OutputStream output) throws IOException, WebApplicationException {
                try (WritableByteChannel outputChannel = Channels.newChannel(output);
                     FileChannel inputChannel = new FileInputStream(asset).getChannel()) {
                    inputChannel.transferTo(0, inputChannel.size(), outputChannel);
                }
            }
        };
    }
}

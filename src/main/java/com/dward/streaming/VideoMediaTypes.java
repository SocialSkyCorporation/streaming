package com.dward.streaming;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class VideoMediaTypes {
    public static final String AVI = "video/avi"; //
    public static final String EXAMPLE = "video/example"; //: example in documentation, Defined in RFC 4735
    public static final String MPEG = "video/mpeg"; //: MPEG-1 video with multiplexed audio; Defined in RFC 2045 and RFC 2046
    public static final String MP4 = "video/mp4"; //: MP4 video; Defined in RFC 4337
    public static final String OGG = "video/ogg"; //: Ogg Theora or other video (with audio); Defined in RFC 5334
    public static final String QUICKTIME = "video/quicktime"; //: QuickTime video; Registered[16]
    public static final String WEBM = "video/webm"; //: WebM Matroska-based open media format
    public static final String MATROSKA = "video/x-matroska"; //: Matroska open media format
    public static final String WMV = "video/x-ms-wmv"; //: Windows Media Video; Documented in Microsoft KB 288102
    public static final String FLV = "video/x-flv"; //: Flash video (FLV files)

    public static String parse(String fileName) {
        final String[] split = fileName.split("\\.");
        final String extension = split[split.length - 1].toLowerCase().trim();
        switch (extension) {
            case "mpeg":
                return MPEG;
            case "mp4":
                return MP4;
            case "ogg":
                return OGG;
            case "flv":
                return FLV;
            case "wmv":
                return WMV;
            case "avi":
                return AVI;
            default:
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
    }
}

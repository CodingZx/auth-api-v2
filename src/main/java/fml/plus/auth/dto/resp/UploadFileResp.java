package fml.plus.auth.dto.resp;

import lombok.Data;

@Data
public class UploadFileResp {
    private String url;

    public UploadFileResp(String url) {
        this.url = url;
    }
}

package com.example.imusic;

public class AudioData {
    private String name;
    private String path;
    private String albumId;

    public AudioData(String name, String path, String albumId) {
        this.name = name;
        this.path = path;
        this.albumId = albumId;
    }

    public AudioData(){
        super();
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

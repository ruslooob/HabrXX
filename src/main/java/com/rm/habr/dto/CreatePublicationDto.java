package com.rm.habr.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CreatePublicationDto {
    @NotNull
    @Size(min = 2, max = 100, message = "Размер заголовка должен быть в пределах от 2 до 200 символов!")
    public String header;
    @NotNull
    @Size(min = 100, max = 1000000, message = "Размер содержимого поста должен быть в пределах от 100 до 1000000 символов!")
    public String content;
    @NotNull
    //todo check mime-type
    public MultipartFile previewImage;
    @NotNull
    @Size(min = 1, message = "Количество жанров должно быть минимум 1")
    public Long[] genreIds;
    @NotNull
    @Size(min = 1, message = "Количество тэгов должно быть минимум 1")
    public Long[] tagIds;
}


//public record CreatePublicationDto(
//        @NotNull
//        @Size(min = 2, max = 100,
//                message = "Размер заголовка должен быть в пределах от 2 до 200 символов!")
//        String header,
//
//        @NotNull
//        @Size(min = 100, max = 1000000,
//                message = "Размер содержимого поста должен быть в пределах от 100 до 1000000 символов!")
//        String content,
//
//        @NotNull
//        //todo check mime-type
//        MultipartFile previewImage,
//
//        @NotNull
//        @Size(min = 1, message = "Количество жанров должно быть минимум 1")
//        Long[] genreIds,
//
//        @NotNull
//        @Size(min = 1, message = "Количество тэгов должно быть минимум 1")
//        Long[] tagIds
//) {
//    public CreatePublicationDto() {
//        this(null, null, null, null, null);
//    }
//}

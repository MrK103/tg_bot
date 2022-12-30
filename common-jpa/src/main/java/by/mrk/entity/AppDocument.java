package by.mrk.entity;

import lombok.*;

import javax.persistence.*;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "app_document")
public class AppDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String telegramField;
    private String documentName;

    @OneToOne
    private BinaryContent binaryContent;

    private String mimeType;
    private Long fileSize;
}

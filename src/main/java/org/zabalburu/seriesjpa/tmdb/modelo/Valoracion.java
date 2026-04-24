package org.zabalburu.seriesjpa.tmdb.modelo;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;


@Entity
@Table(name = "valoraciones")
@SequenceGenerator(name = "seq_valoracion", sequenceName = "SEQ_VALORACION", allocationSize = 1)
public class Valoracion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_valoracion")
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private int estrellas;

    @Column(length = 1000)
    private String comentario;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public void setId(Long id) {
        this.id = id;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "serie_id", nullable = false)
    private Serie serie;

    public Valoracion() {
    }

    public Valoracion(int estrellas, String comentario) {
        setEstrellas(estrellas);
        this.comentario = comentario;
        this.fecha = LocalDate.now();
    }

    public Valoracion(Usuario usuario, Serie serie, int estrellas, String comentarios, LocalDate now) {
        this(estrellas, comentarios);
        this.setSerie(serie);
        this.setUsuario(usuario);
        this.setFecha(fecha);
    }

    public void setEstrellas(int estrellas) {
        if (estrellas < 1 || estrellas > 5) {
            throw new IllegalArgumentException("Las estrellas deben estar entre 1 y 5");
        }
        this.estrellas = estrellas;
    }

    public void actualizar(int estrellas, String comentario) {
        setEstrellas(estrellas);
        this.comentario = comentario;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public int getEstrellas() {
        return estrellas;
    }

    public String getComentario() {
        return comentario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Serie getSerie() {
        return serie;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Valoracion that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

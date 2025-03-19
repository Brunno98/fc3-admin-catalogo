package br.com.brunno.admin.catalogo.domain;

import br.com.brunno.admin.catalogo.domain.castmember.CastMemberType;
import br.com.brunno.admin.catalogo.domain.video.Rating;
import br.com.brunno.admin.catalogo.domain.resource.Resource;
import br.com.brunno.admin.catalogo.domain.video.VideoMediaType;
import com.github.javafaker.Faker;
import io.vavr.API;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.List;

public final class Fixture {

    private Fixture() {}

    private static final Faker FAKER = new Faker();

    public static String name() {
        return FAKER.name().fullName();
    }

    public static Integer year() {
        return FAKER.random().nextInt(1800, 2100);
    }

    public static Double duration() {
        final var power = FAKER.random().nextInt(1, 3);
        final var decimalCases = Math.pow(10, power);
        final var value = FAKER.random().nextDouble() * decimalCases;
        return Math.round(value * 100.0) / 100.0; // Mantém 2 casas decimais
    }

    public static boolean bool() {
        return FAKER.bool().bool();
    }

    public static String title () {
        return FAKER.options()
                .option(
                        "Sombras do Amanhã",
                        "O Último Código",
                        "Tempestade Lunar",
                        "Ruínas do Destino",
                        "A Máquina do Infinito"
                );
    }

    public static final class CastMembers {
        public static CastMemberType type() {
            return FAKER.options().option(CastMemberType.values());
        }
    }
    public static final class Videos {
        public static String description() {
            return FAKER.options().option(
                            """
                            Um grupo de desconhecidos se vê preso em um lugar isolado após um evento inesperado mudar
                            suas vidas para sempre. Forçados a confiar uns nos outros, eles precisam enfrentar desafios
                            mortais, resolver mistérios e descobrir a verdade por trás do que os levou até ali antes que
                            seja tarde demais.""",
                    """
                            Um herói improvável, sem nenhuma experiência em combates ou aventuras, é arrastado para uma
                            jornada perigosa quando descobre uma relíquia poderosa que pode mudar o destino do mundo.
                            Agora, perseguido por inimigos implacáveis, ele deve aprender a lutar, confiar em aliados
                            inesperados e impedir que a ameaça caia em mãos erradas.""",
                    """
                            Após encontrar pistas sobre um segredo oculto há séculos, um investigador obstinado começa
                            a desvendar uma conspiração que pode mudar a história da humanidade. Mas quanto mais se
                            aproxima da verdade, mais se torna alvo de forças poderosas que farão de tudo para
                            silenciá-lo e proteger o que foi enterrado no passado.""",
                    """
                            Em um futuro distópico onde o governo controla todos os aspectos da vida, um rebelde
                            solitário descobre evidências de que a sociedade vive uma mentira. Agora, ele precisa
                            desafiar o sistema, unir forças com um grupo clandestino e enfrentar a dura realidade de
                            uma revolução que pode custar sua vida, mas salvar toda a população.""",
                    """
                            Quando um incidente misterioso abala uma pacata cidade do interior, um detetive solitário e
                            cético começa a investigar e descobre pistas que o levam a uma conspiração há muito
                            escondida. Conforme se aprofunda no caso, ele percebe que os envolvidos não são quem
                            aparentam e que algumas verdades podem ser perigosas demais para serem reveladas."""

            );
        }

        public static Rating rating() {
            return FAKER.options().option(Rating.class);
        }

        public static Resource resource(VideoMediaType type) {
            final String contentType = API.Match(type).of(
                    Case($(List(VideoMediaType.VIDEO, VideoMediaType.TRAILER)::contains), "video/mp4"),
                    Case($(), "image/jpg")
            );
            final String checksum = "checksum";
            final byte[] content = "Conteudo".getBytes();

            return Resource.with(checksum, content, contentType, type.name().toLowerCase());
        }
    }
}

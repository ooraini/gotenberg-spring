package dev.gotenberg;

import org.springframework.boot.docker.compose.core.RunningService;
import org.springframework.boot.docker.compose.service.connection.DockerComposeConnectionDetailsFactory;
import org.springframework.boot.docker.compose.service.connection.DockerComposeConnectionSource;

class GotenbergDockerComposeConnectionDetailsFactory
		extends DockerComposeConnectionDetailsFactory<GotenbergConnectionDetails> {

	private static final String[] IMAGE_NAMES = { "gotenberg/gotenberg" };

	private static final int HTTP_PORT = 3000;

	GotenbergDockerComposeConnectionDetailsFactory() {
		super(IMAGE_NAMES);
	}

	@Override
	protected GotenbergConnectionDetails getDockerComposeConnectionDetails(DockerComposeConnectionSource source) {
		return new OtlpLoggingDockerComposeConnectionDetails(source.getRunningService());
	}

	private static final class OtlpLoggingDockerComposeConnectionDetails extends DockerComposeConnectionDetails
			implements GotenbergConnectionDetails {

		private final String host;


		private final int port;

		private OtlpLoggingDockerComposeConnectionDetails(RunningService source) {
			super(source);
			this.host = source.host();
			this.port = source.ports().get(HTTP_PORT);
		}

		@Override
		public String baseUrl() {
			return "http://%s:%d".formatted(this.host, port);
		}
	}

}

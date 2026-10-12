# Mobile Spotify Service

A Spring Boot backend that syncs a user's Spotify listening history for the current day. A mobile client sends its Spotify access token, and the service fetches recently played tracks from the Spotify Web API and returns them in a clean format.

## Tech Stack

- Java 17
- Spring Boot 4 (Web, WebFlux WebClient, Validation, Actuator)
- Lombok
- Maven, Docker

## Project Structure

```
Spotify-History-Backend/
  src/main/java/.../
    controllers/     REST endpoint
    services/        Sync logic and Spotify API client
    dtos/            Request and response models
    configurations/  CORS and WebClient setup
  Dockerfile
  pom.xml
```

## Configuration

No secrets are stored in this repo. The Spotify access token is supplied by the client on each request.

| Variable | Default | Description          |
|----------|---------|----------------------|
| `PORT`   | `8080`  | Port the server runs on |

If you add secrets later, keep them in a local `.env` file (ignored by git) or your hosting platform's environment settings.

## Running Locally

```bash
cd Spotify-History-Backend
./mvnw spring-boot:run
```

## Running with Docker

```bash
cd Spotify-History-Backend
docker build -t spotify-history-backend .
docker run -p 8080:8080 spotify-history-backend
```

## API

### `POST /api/history/sync`

Headers:

```
Authorization: Bearer <spotify_access_token>
Content-Type: application/json
```

Body:

```json
{
  "timezone": "Europe/London",
  "currentFileLocalDate": "2026-10-12",
  "lastPlayedAtMs": 1760252400000
}
```

- `timezone` (required): the user's IANA timezone.
- `currentFileLocalDate`: the date of the client's current history file. If it is not today, the sync resets and starts from midnight.
- `lastPlayedAtMs`: timestamp of the last track the client already has. Only newer plays are returned.

Response:

```json
{
  "reset": false,
  "localDate": "2026-10-12",
  "timezone": "Europe/London",
  "newLastPlayedAtMs": 1760259600000,
  "items": [
    {
      "trackName": "Song Name",
      "artistNames": ["Artist"],
      "playedAt": "2026-10-12T09:00:00Z",
      "playedAtMs": 1760259600000,
      "albumImageUrl": "https://..."
    }
  ]
}
```

### `GET /actuator/health`

Health check for hosting platforms.

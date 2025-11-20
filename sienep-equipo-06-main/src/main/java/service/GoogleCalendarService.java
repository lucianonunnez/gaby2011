package service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class GoogleCalendarService {
    private static final Logger logger = Logger.getLogger(GoogleCalendarService.class);
    private static final String APPLICATION_NAME = "SIENEP - Sistema de Seguimiento";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private Calendar service;

    public GoogleCalendarService() throws GeneralSecurityException, IOException{
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException{
        // cargar secretos del cliente
        InputStream in = GoogleCalendarService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if(in == null) {
            throw new FileNotFoundException("Archivo de credenciales no encontrado: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // build flow & trigger user authorization request
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * Crea un evento en Google Calendar con recordatorio
     * @param titulo Título del evento
     * @param descripcion Descripción del evento
     * @param fechaHora Fecha y hora del evento
     * @param duracionMinutos Duración en minutos
     * @param recordatorioMinutos Minutos antes para el recordatorio
     * @return ID del evento creado
     */

    public String crearEventoConRecordatorio(String titulo, String descripcion,
                                             LocalDateTime fechaHora, int duracionMinutos,
                                             int recordatorioMinutos) throws IOException {
        Event event = new Event()
                .setSummary(titulo)
                .setDescription(descripcion);

        // convertir LocalDateTime a Date y luego a DateTime de Google
        Date startDate = Date.from(fechaHora.atZone(ZoneId.systemDefault()).toInstant());
        DateTime startDateTime = new DateTime(startDate);
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("America/Montevideo");

        // calcular fecha de fin
        LocalDateTime fechaFin = fechaHora.plusMinutes(duracionMinutos);
        Date endDate = Date.from(fechaFin.atZone(ZoneId.systemDefault()).toInstant());
        DateTime endDateTime = new DateTime(endDate);
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("America/Montevideo");

        event.setStart(start);
        event.setEnd(end);

        // configurar recordatorio
        EventReminder[] reminderOverrides = new EventReminder[]{
                new EventReminder().setMethod("popup").setMinutes(recordatorioMinutos),
                new EventReminder().setMethod("email").setMinutes(recordatorioMinutos)
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(List.of(reminderOverrides));
        event.setReminders(reminders);

        // insert evento en el calendario
        String calendarId = "primary";
        event = service.events().insert(calendarId, event).execute();

        logger.info("Evento creado en Google Calendar: " + event.getHtmlLink());
        return event.getId();
    }

    /**
     * Actualiza un evento existente
     */
    public void actualizarEvento(String eventId, String titulo, String descripcion,
                                 LocalDateTime fechaHora) throws IOException {
        Event event = service.events().get("primary", eventId).execute();

        event.setSummary(titulo);
        event.setDescription(descripcion);

        Date startDate = Date.from(fechaHora.atZone(ZoneId.systemDefault()).toInstant());
        DateTime startDateTime = new DateTime(startDate);
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("America/Montevideo");
        event.setStart(start);

        Event updatedEvent = service.events().update("primary", eventId, event).execute();
        logger.info("Evento actualizado en Google Calendar: " + updatedEvent.getHtmlLink());
    }

    /**
     * Elimina un evento
     */
    public void eliminarEvento(String eventId) throws IOException {
        service.events().delete("primary", eventId).execute();
        logger.info("Evento eliminado de Google Calendar: " + eventId);
    }
}

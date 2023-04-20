package tacos.actuator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.DeleteOperation;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@Endpoint(id="notes", enableByDefault = true)
public class NotesEndpoint {

    private List<Note> notes = new ArrayList<>();

    @ReadOperation
    public List<Note> notes(){
        return notes;
    }

    @WriteOperation
    public List<Note> addNote(String text){
        notes.add(new Note(text));
        return notes;
    }

    @DeleteOperation
    public List<Note> deleteNote(int index){
        if(index < notes.size()){
            notes.remove(index);
        }
        return notes;
    }


    @Getter
    @RequiredArgsConstructor
    private class Note{

        private LocalDate time = LocalDate.now();
        private final String text;
    }
}

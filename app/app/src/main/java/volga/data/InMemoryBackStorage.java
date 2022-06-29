package volga.data;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.List;
import java.util.Map;
import java.util.Set;

import volga.model.Preset;

@RequiresApi(api = Build.VERSION_CODES.R)
public class InMemoryBackStorage implements Storage {

    private final Preset activePreset;

    private List<Preset> currentPresets;

    public InMemoryBackStorage() {
        currentPresets = List.of(
                new Preset("gym12", Map.of("7795", Set.of("208", "56", "7"))),
                new Preset("river", Map.of("8220", Set.of("208", "56", "7", "55", "6")))
        );
        activePreset = getPresetByName("gym12");
    }

    @Override
    public Preset getActivePreset() {
        return activePreset;
    }

    private Preset getPresetByName(String searchedName) {
        return currentPresets.stream().filter(preset -> preset.getName().equals(searchedName)).findFirst().get();
    }
}
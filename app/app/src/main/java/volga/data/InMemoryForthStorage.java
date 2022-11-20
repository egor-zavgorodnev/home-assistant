package volga.data;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.List;
import java.util.Map;
import java.util.Set;

import volga.model.Preset;

@RequiresApi(api = Build.VERSION_CODES.R)
public class InMemoryForthStorage implements Storage {

    private final Preset activePreset;

    private final List<Preset> currentPresets;

    public InMemoryForthStorage() {
        currentPresets = List.of(
                new Preset("home", Map.of("12388", Set.of("7"))
                ));
        activePreset = getPresetByName("home");
    }

    @Override
    public Preset getActivePreset() {
        return activePreset;
    }

    private Preset getPresetByName(String searchedName) {
        return currentPresets.stream().filter(preset -> preset.getName().equals(searchedName)).findFirst().get();
    }
}

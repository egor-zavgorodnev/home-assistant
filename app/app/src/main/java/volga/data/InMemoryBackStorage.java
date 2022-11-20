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
                new Preset("swimmingpool", Map.of("12407", Set.of("7"), "12392", Set.of("55")))
        );
        activePreset = getPresetByName("swimmingpool");
    }

    @Override
    public Preset getActivePreset() {
        return activePreset;
    }

    private Preset getPresetByName(String searchedName) {
        return currentPresets.stream().filter(preset -> preset.getName().equals(searchedName)).findFirst().get();
    }
}
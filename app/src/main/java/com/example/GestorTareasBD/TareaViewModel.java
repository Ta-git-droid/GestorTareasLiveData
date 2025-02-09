package com.example.GestorTareasBD;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class TareaViewModel extends ViewModel {
    private final MutableLiveData<List<Tarea>> listaTareas = new MutableLiveData<>();
    private BaseDatosTareas baseDatosTareas;

    public void init(Context context) {
        baseDatosTareas = new BaseDatosTareas(context);
        cargarTareas();
    }

    public LiveData<List<Tarea>> getTareas() {
        return listaTareas;
    }

    private void cargarTareas() {
        List<Tarea> tareas = baseDatosTareas.obtenerTareas();
        listaTareas.setValue(tareas);
    }

    public void agregarTarea(Tarea tarea) {
        baseDatosTareas.agregarTarea(tarea);
        cargarTareas();
    }

    public void actualizarTarea(Tarea tarea) {
        baseDatosTareas.actualizarTarea(tarea);
        cargarTareas();
    }

    public void eliminarTarea(Tarea tarea) {
        baseDatosTareas.eliminarTarea(tarea.getId());
        cargarTareas();
    }

    public void guardarTarea(Tarea tarea) {
        baseDatosTareas.agregarTarea(tarea);
        cargarTareas();
    }
}
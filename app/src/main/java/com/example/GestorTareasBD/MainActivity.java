package com.example.GestorTareasBD;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tarea_7_gestortareas.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;  // Vista para mostrar una lista de tareas
    private TareaAdapter adaptador;     // Adaptador para conectar la lista de tareas con la vista
    private List<Tarea> listaTareas;    // Lista que contiene las tareas a mostrar
    private TareaViewModel tareaViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Configurar diseño sin bordes (opcional)
        setContentView( R.layout.activity_main);

        // Configurar la disposición de la actividad principal para adaptarse a los bordes del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_main), (vista, insets) -> {
            Insets bordesSistema = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            vista.setPadding(bordesSistema.left, bordesSistema.top, bordesSistema.right, bordesSistema.bottom);
            return insets;
        });

        // Inicializar vistas
        recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton botonFlotante = findViewById(R.id.fab);

        listaTareas = new ArrayList<>();
        adaptador = new TareaAdapter(new ArrayList<>(), this::mostrarOpcionesTarea);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adaptador);

        // Inicializar ViewModel
        tareaViewModel = new ViewModelProvider(this).get(TareaViewModel.class);
        tareaViewModel.init(getApplicationContext());

        // Configurar observadores para LiveData
        configurarObservadores();

        botonFlotante.setOnClickListener(v -> mostrarDialogoTarea(null));

        tareaViewModel.getTareas().observe(this, tareas -> {
            adaptador.setTareas(tareas); // Usar el método setTareas del adaptador
            ordenarTareas();
        });
    }

    private void configurarObservadores() {
        // Observador de LiveData para la lista de tareas
        tareaViewModel.getTareas().observe(this, tareas -> {
            listaTareas.clear();
            listaTareas.addAll(tareas);
            ordenarTareas();
            adaptador.notifyDataSetChanged();  // Notificar al adaptador que los datos han cambiado
        });
    }

    private void agregarTareaBD(Tarea tarea) {
        // Agregar tarea usando el ViewModel
        tareaViewModel.agregarTarea(tarea);
    }

    private void eliminarTareaBD(Tarea tarea) {
        // Eliminar tarea usando el ViewModel
        tareaViewModel.eliminarTarea(tarea);
    }

    private void actualizarTareaBD(Tarea tarea) {
        // Actualizar tarea usando el ViewModel
        tareaViewModel.actualizarTarea(tarea);
    }

    private void ordenarTareas() {
        listaTareas.sort((t1, t2) -> {
            int asignaturaComparison = t1.getAsignatura().compareTo(t2.getAsignatura());
            if (asignaturaComparison != 0) return asignaturaComparison;

            try {
                SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
                Date fecha1 = formatoFecha.parse(t1.getFechaEntrega());
                Date fecha2 = formatoFecha.parse(t2.getFechaEntrega());
                return fecha1.compareTo(fecha2);
            } catch (ParseException e) {
                Log.e("MainActivity", "Error al parsear fechas", e);
                return 0;
            }
        });
        adaptador.notifyDataSetChanged();
    }

    private void mostrarDialogoTarea(Tarea tareaAEditar) {
        NuevaTareaDialogFragment dialogo = new NuevaTareaDialogFragment();
        if (tareaAEditar != null) {
            Bundle argumentos = new Bundle();
            argumentos.putParcelable("homework", tareaAEditar);
            dialogo.setArguments(argumentos);
        }

        dialogo.setOnTareaGuardadaListener();

        dialogo.show(getSupportFragmentManager(), "DialogoTarea");
    }

    private void mostrarOpcionesTarea(Tarea tarea) {
        BottomSheetDialog dialogoOpciones = new BottomSheetDialog(this);
        View vistaOpciones = getLayoutInflater().inflate(R.layout.tareas_opciones, null);

        vistaOpciones.findViewById(R.id.editOption).setOnClickListener(v -> {
            dialogoOpciones.dismiss();
            mostrarDialogoTarea(tarea);
        });

        vistaOpciones.findViewById(R.id.deleteOption).setOnClickListener(v -> {
            dialogoOpciones.dismiss();
            confirmarEliminacion(tarea);
        });

        vistaOpciones.findViewById(R.id.completeOption).setOnClickListener(v -> {
            dialogoOpciones.dismiss();
            tarea.setEstaCompletada(true);
            actualizarTareaBD(tarea);
            Toast.makeText(this, "Tarea marcada como completada", Toast.LENGTH_SHORT).show();
        });

        dialogoOpciones.setContentView(vistaOpciones);
        dialogoOpciones.show();
    }

    private void confirmarEliminacion(Tarea tarea) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar esta tarea?")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarTareaBD(tarea))
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
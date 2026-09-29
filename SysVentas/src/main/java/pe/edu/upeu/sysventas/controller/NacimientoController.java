package pe.edu.upeu.sysventas.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import pe.edu.upeu.sysventas.components.ColumnInfo;
import pe.edu.upeu.sysventas.components.TableViewHelper;
import pe.edu.upeu.sysventas.components.Toast;
import pe.edu.upeu.sysventas.components.ToltipCustom;
import pe.edu.upeu.sysventas.model.Nacimiento;
import pe.edu.upeu.sysventas.service.INacimientoService;

import java.util.*;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class NacimientoController {
    private final INacimientoService ns;

    @FXML
    TextField txtNombreRecienNacido, txtHospital, txtNombrePadre, txtNombreMadre, txtFiltroDato;
    @FXML
    DatePicker dpFechaNacimiento, dpDesde, dpHasta;

    @FXML private TableView<Nacimiento> tableView;

    @FXML
    Label lbnMsg, lblContador;
    @FXML private AnchorPane miContenedor;
    Stage stage;

    private Validator validator;
    Nacimiento formulario;
    Long idNacimientoCE = 0L;

    private final ToltipCustom ttc = new ToltipCustom();

    @FXML
    public void initialize() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        TableViewHelper<Nacimiento> tableViewHelper = new TableViewHelper<>();

        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("ID", new ColumnInfo("idNacimiento", 60.0));
        columns.put("Recién Nacido", new ColumnInfo("nombreRecienNacido", 200.0));
        columns.put("Fecha Nacimiento", new ColumnInfo("fechaNacimiento", 130.0));
        columns.put("Hospital / Lugar", new ColumnInfo("hospital", 200.0));
        columns.put("Padre", new ColumnInfo("nombrePadre", 180.0));
        columns.put("Madre", new ColumnInfo("nombreMadre", 180.0));

        Consumer<Nacimiento> updateAction = nacimiento -> { editForm(nacimiento); };
        Consumer<Nacimiento> deleteAction = nacimiento -> {
            stage = (Stage) miContenedor.getScene().getWindow();
            ns.delete(nacimiento.getIdNacimiento());
            double w = stage.getWidth() / 1.5, h = stage.getHeight() / 2;
            Toast.showToast(stage, "Se eliminó correctamente!!", 2000, w, h);
            listar();
        };

        tableViewHelper.addColumnsInOrderWithSize(tableView, columns, updateAction, deleteAction);
        tableView.setTableMenuButtonVisible(true);
        listar();
    }

    public void listar() {
        try {
            mostrarEnTabla(ns.findAll());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // Botón Filtrar
    @FXML
    public void filtrar() {
        mostrarEnTabla(ns.filtrar(txtFiltroDato.getText(), dpDesde.getValue(), dpHasta.getValue()));
    }

    // Botón Limpiar filtro
    @FXML
    public void limpiarFiltro() {
        txtFiltroDato.clear();
        dpDesde.setValue(null);
        dpHasta.setValue(null);
        listar();
    }

    private void mostrarEnTabla(List<Nacimiento> lista) {
        tableView.getItems().clear();
        tableView.getItems().addAll(FXCollections.observableArrayList(lista));
        lblContador.setText("Mostrando " + lista.size() + " de " + ns.findAll().size() + " registros");
    }

    @FXML
    public void validarFormulario() {
        formulario = new Nacimiento();
        formulario.setNombreRecienNacido(txtNombreRecienNacido.getText());
        formulario.setFechaNacimiento(dpFechaNacimiento.getValue());
        formulario.setHospital(txtHospital.getText());
        formulario.setNombrePadre(txtNombrePadre.getText());
        formulario.setNombreMadre(txtNombreMadre.getText());

        Set<ConstraintViolation<Nacimiento>> violaciones = validator.validate(formulario);
        List<ConstraintViolation<Nacimiento>> violacionesOrdenadas = violaciones.stream()
                .sorted(Comparator.comparing(v -> v.getPropertyPath().toString())).toList();

        if (violacionesOrdenadas.isEmpty()) {
            procesarFormulario();
        } else {
            mostrarErroresValidacion(violacionesOrdenadas);
        }
    }

    // Botón Cancelar
    @FXML
    public void clearForm() {
        txtNombreRecienNacido.clear();
        dpFechaNacimiento.setValue(null);
        txtHospital.clear();
        txtNombrePadre.clear();
        txtNombreMadre.clear();
        idNacimientoCE = 0L;
        lbnMsg.setText("");
        limpiarError();
    }

    public void editForm(Nacimiento nacimiento) {
        txtNombreRecienNacido.setText(nacimiento.getNombreRecienNacido());
        dpFechaNacimiento.setValue(nacimiento.getFechaNacimiento());
        txtHospital.setText(nacimiento.getHospital());
        txtNombrePadre.setText(nacimiento.getNombrePadre());
        txtNombreMadre.setText(nacimiento.getNombreMadre());
        idNacimientoCE = nacimiento.getIdNacimiento();
        limpiarError();
    }

    private void mostrarErroresValidacion(List<ConstraintViolation<Nacimiento>> violaciones) {
        limpiarError();
        Map<String, Control> campos = new LinkedHashMap<>();
        campos.put("nombreRecienNacido", txtNombreRecienNacido);
        campos.put("fechaNacimiento", dpFechaNacimiento);
        campos.put("hospital", txtHospital);
        campos.put("nombrePadre", txtNombrePadre);
        campos.put("nombreMadre", txtNombreMadre);

        LinkedHashMap<String, String> erroresOrdenados = new LinkedHashMap<>();
        final Control[] primerCtrl = {null};
        for (String campo : campos.keySet()) {
            violaciones.stream()
                    .filter(v -> v.getPropertyPath().toString().equals(campo))
                    .findFirst().ifPresent(v -> {

                        erroresOrdenados.put(campo, v.getMessage());

                        Control c = campos.get(campo);
                        if (c != null) ttc.marcarError(c, v.getMessage().trim());
                        if (primerCtrl[0] == null) primerCtrl[0] = c;
                    });
        }
        if (!erroresOrdenados.isEmpty()) {
            lbnMsg.setText(erroresOrdenados.entrySet().iterator().next().getValue());
            lbnMsg.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
            if (primerCtrl[0] != null) Platform.runLater(primerCtrl[0]::requestFocus);
        }
    }

    private void procesarFormulario() {
        stage = (Stage) miContenedor.getScene().getWindow();
        limpiarError();
        double w = stage.getWidth() / 1.5, h = stage.getHeight() / 2;
        if (idNacimientoCE > 0L) {
            formulario.setIdNacimiento(idNacimientoCE);
            ns.update(idNacimientoCE, formulario);
            Toast.showToast(stage, "Se actualizó correctamente!!", 2000, w, h);
        } else {
            ns.save(formulario);
            Toast.showToast(stage, "Se guardó correctamente!!", 2000, w, h);
        }
        clearForm();
        listar();
    }

    public void limpiarError() {
        List.of(txtNombreRecienNacido, dpFechaNacimiento, txtHospital, txtNombrePadre, txtNombreMadre)
                .forEach(c -> {
                    ttc.limpiarCampo(c);
                });
    }

}

package dclient.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dclient.DClient;
import dclient.controllers.visualModels.RSPPtableElement;
import dclient.db.dao.RsppDAO;
import dclient.model.Model;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainController {
	@FXML
	private TextField searchField;

	@FXML
	private TableView<RSPPtableElement> rsppTable;

	@FXML
	private TableColumn<RSPPtableElement, String> nameColumn;

	@FXML
	private TableColumn<RSPPtableElement, LocalDate> deadlineColumn;

	@FXML
	private TableColumn<RSPPtableElement, String> invoiceColumn;

	@FXML
	private TableColumn<RSPPtableElement, Boolean> payedColumn;

	@FXML
	private TableColumn<RSPPtableElement, String> noteColumn;

	@FXML
	private CheckBox checkBoxPayed;

	@FXML
	private CheckBox checkBoxDeadline;

	@FXML
	private Button createNewButton;

	@FXML
	private Button viewEditButton;

	private static Logger logger = LoggerFactory.getLogger("DClient");

	private ObservableList<RSPPtableElement> rsppElements;
	private FilteredList<RSPPtableElement> filteredrsppElements;
	private SortedList<RSPPtableElement> sortedFilteredrsppElements;

	Model model;

	public void createTable() {
		rsppElements = FXCollections
				.observableArrayList(RsppDAO.getRSPPTable(model.getConMan().getDBConnection(), model.getConfig()));
		model.getConMan().closeDBConnection();
		filteredrsppElements = new FilteredList<>(rsppElements);

		nameColumn.setCellValueFactory(new PropertyValueFactory<RSPPtableElement, String>("accountName"));
		deadlineColumn.setCellValueFactory(cellData -> cellData.getValue().jobEndProperty());

		deadlineColumn.setCellFactory(column -> {
			return new TableCell<RSPPtableElement, LocalDate>() {
				@Override
				protected void updateItem(LocalDate item, boolean empty) {
					super.updateItem(item, empty);

					if (item == null || empty) {
						setText(null);
						setStyle("");
					} else {
						setText(DateTimeFormatter.ofPattern(model.getConfig().getProperty("dateFormat", "dd/MM/yyyy"))
								.format(item));
					}
				}
			};
		});
		invoiceColumn.setCellValueFactory(new PropertyValueFactory<RSPPtableElement, String>("invoiceID"));

		payedColumn.setCellValueFactory(f -> f.getValue().payedProperty());
		payedColumn.setCellFactory(tc -> new CheckBoxTableCell<>());

		noteColumn.setCellValueFactory(new PropertyValueFactory<RSPPtableElement, String>("note"));

		final ObjectProperty<Predicate<RSPPtableElement>> textFilter = new SimpleObjectProperty<>();
		final ObjectProperty<Predicate<RSPPtableElement>> dateFilter = new SimpleObjectProperty<>();
		final ObjectProperty<Predicate<RSPPtableElement>> payedFilter = new SimpleObjectProperty<>();

		textFilter.bind(Bindings.createObjectBinding(() -> rspp -> textFilter(rspp), searchField.textProperty()));

		payedFilter
				.bind(Bindings.createObjectBinding(() -> rspp -> payedFilter(rspp), checkBoxPayed.selectedProperty()));

		dateFilter.bind(
				Bindings.createObjectBinding(() -> rspp -> dateFilter(rspp), checkBoxDeadline.selectedProperty()));

		filteredrsppElements.predicateProperty()
				.bind(Bindings.createObjectBinding(() -> textFilter.get().and(dateFilter.get().and(payedFilter.get())),
						textFilter, dateFilter, payedFilter));

		sortedFilteredrsppElements = new SortedList<>(filteredrsppElements);
		rsppTable.setItems(sortedFilteredrsppElements);
		sortedFilteredrsppElements.comparatorProperty().bind(rsppTable.comparatorProperty());

		rsppTable.setRowFactory(tv -> {
			final TableRow<RSPPtableElement> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					switchToViewEdit();
				}
			});
			return row;
		});
	}

	private boolean textFilter(final RSPPtableElement rspp) {
		return (searchField.getText().isEmpty() || rspp.getAccountName() != null || (rspp.getAccountDescriptor() != null
				&& rspp.getAccountDescriptor().toLowerCase().contains(searchField.getText().toLowerCase())));
	}

	private boolean dateFilter(final RSPPtableElement rspp) {
		if (checkBoxDeadline.isSelected()) {
			if (rspp.jobEndDate().isAfter(LocalDate.now()) && rspp.jobEndDate().isBefore(LocalDate.now()
					.plusDays(Integer.parseInt(model.getConfig().getProperty("rsppTable.daysAdvance", "14")))))
				return true;
			else
				return false;
		}
		return true;
	}

	private boolean payedFilter(final RSPPtableElement rspp) {
		if (checkBoxPayed.isSelected()) {
			if (!rspp.getPayed())
				return true;
			else
				return false;
		}
		return true;
	}

	public void setModel(final Model model) {
		this.model = model;
	}

	public void refresh() {
		model.refreshSession();
		createTable();
	}

	@FXML
	public void switchToViewEdit() {
		final RSPPtableElement selectedItems = rsppTable.getSelectionModel().getSelectedItem();
		if (selectedItems != null) {
			try {
				final Stage stage = new Stage();

				final FXMLLoader loader = new FXMLLoader(getClass().getResource("edit.fxml"));
				final VBox root = (VBox) loader.load();
				final ViewEditController controller = loader.getController();

				model.refreshSession();

				controller.setModel(model);
				controller.setCombo();
				controller.setMainControllerRef(this);
				controller.setRSPP(selectedItems.getJobID(), selectedItems.getJobStart());

				final Scene scene = new Scene(root);
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				stage.setScene(scene);
				stage.setTitle("Visualizza e/o modifica " + selectedItems.getAccountName());
				stage.getIcons().add(new Image(DClient.class.getResourceAsStream("logo.png")));
				stage.show();

			} catch (final Exception e) {
				e.printStackTrace();
			}
		} else {
			final Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Attenzione!");
			alert.setHeaderText("Riga RSPP non selezionata");
			alert.setContentText(
					"Come fare?\nPer utilizzare la funziona \"visualizza e modifica\" di questo programma occorre selezionare la riga della tabella e successivamente procedere cliccando il pulsante.");

			alert.showAndWait();
		}

	}

	public void switchToNewView() {
		try {
			final Stage stage = new Stage();

			final FXMLLoader loader = new FXMLLoader(getClass().getResource("new.fxml"));
			final VBox root = (VBox) loader.load();
			final NewRSPPController controller = loader.getController();

			controller.initController(this, model);

			final Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setScene(scene);
			stage.setTitle("Crea un nuovo RSPP");
			stage.setMinHeight(745);
			stage.setMinWidth(745);
			stage.getIcons().add(new Image(App.class.getResourceAsStream("logo.png")));
			stage.show();

		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
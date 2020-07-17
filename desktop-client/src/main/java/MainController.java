import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.Predicate;

import db.SicurteaDAO;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Model;

public class MainController {
	public final long DAYS_ADVANCE = 14;

	@FXML
	private TextField searchField;

	@FXML
	private TableView<RSPPtableElement> rsppTable;

	@FXML
	private TableColumn<RSPPtableElement, String> nameColumn;

	@FXML
	private TableColumn<RSPPtableElement, String> categoryColumn;

	@FXML
	private TableColumn<RSPPtableElement, String> deadlineColumn;

	@FXML
	private TableColumn<RSPPtableElement, String> invoiceColumn;

	@FXML
	private TableColumn<RSPPtableElement, String> payedColumn;

	@FXML
	private CheckBox checkBoxDeadline;

	@FXML
	private Button viewEditButton;

	private ObservableList<RSPPtableElement> rsppElements;
	private FilteredList<RSPPtableElement> filteredrsppElements;
	private SortedList<RSPPtableElement> SortedFilteredrsppElements;

	SicurteaDAO dao;
	Model model;

	@FXML
	public void initialize() {
		dao = new SicurteaDAO();

		rsppElements = FXCollections.observableArrayList();

		for (Map<String, String> rsppElement : dao.getDataForTable(false)) {
			rsppElements.add(new RSPPtableElement(rsppElement));
		}

		filteredrsppElements = new FilteredList<RSPPtableElement>(rsppElements);

		nameColumn.setCellValueFactory(new PropertyValueFactory<RSPPtableElement, String>("accountName"));
		categoryColumn.setCellValueFactory(new PropertyValueFactory<RSPPtableElement, String>("category"));
		deadlineColumn.setCellValueFactory(new PropertyValueFactory<RSPPtableElement, String>("jobEnd"));
		invoiceColumn.setCellValueFactory(new PropertyValueFactory<RSPPtableElement, String>("invoiceID"));
		payedColumn.setCellValueFactory(new PropertyValueFactory<RSPPtableElement, String>("payed"));

		System.out.println(searchField.getText());

		ObjectProperty<Predicate<RSPPtableElement>> nameFilter = new SimpleObjectProperty<>();
		ObjectProperty<Predicate<RSPPtableElement>> dateFilter = new SimpleObjectProperty<>();

		nameFilter.bind(Bindings.createObjectBinding(
				() -> rspp -> rspp.getAccountName().toLowerCase().contains(searchField.getText().toLowerCase()),
				searchField.textProperty()));

		dateFilter.bind(
				Bindings.createObjectBinding(() -> rspp -> dateFilter(rspp), checkBoxDeadline.selectedProperty()));

		filteredrsppElements.predicateProperty().bind(
				Bindings.createObjectBinding(() -> nameFilter.get().and(dateFilter.get()), nameFilter, dateFilter));

		SortedFilteredrsppElements = new SortedList<>(filteredrsppElements);

		rsppTable.setItems(SortedFilteredrsppElements);

		rsppTable.setRowFactory(tv -> {
			TableRow<RSPPtableElement> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					switchToViewEdit();
				}
			});
			return row;
		});
	}

	private boolean dateFilter(RSPPtableElement rspp) {
		if (checkBoxDeadline.isSelected()) {
			if (rspp.jobEndDate().isAfter(LocalDate.now())
					&& rspp.jobEndDate().isBefore(LocalDate.now().plusDays(DAYS_ADVANCE)))
				return true;
			else
				return false;
		}
		return true;
	}

	public class RSPPtableElement {
		DateTimeFormatter formatter;

		String jobID;
		LocalDate jobStart;

		LocalDate jobEnd;
		StringProperty accountName;
		StringProperty category;
		StringProperty invoiceID;
		StringProperty payed;

		public RSPPtableElement(Map<String, String> rsspElement) {
			formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

			this.jobID = rsspElement.get("jobid");
			this.jobStart = LocalDate.parse(rsspElement.get("jobstart"), formatter);
			this.jobEnd = LocalDate.parse(rsspElement.get("jobend"), formatter);
			this.accountName = new SimpleStringProperty(rsspElement.get("name"));
			switch (rsspElement.get("category")) {
			case "b2b":
				this.category = new SimpleStringProperty("Azienda");
				break;
			case "b2c":
				this.category = new SimpleStringProperty("Privato");
				break;
			case "pa":
				this.category = new SimpleStringProperty("Pubblica Amministrazione");
				break;
			default:
				throw new IllegalArgumentException();
			}

			this.invoiceID = new SimpleStringProperty(rsspElement.get("invoiceid"));

			if (rsspElement.get("payed") == "true")
				payed = new SimpleStringProperty("✔");
			else
				payed = new SimpleStringProperty("");
		}

		public StringProperty accountNameProperty() {
			return accountName;
		}

		public String getAccountName() {
			return accountName.get();
		}

		public StringProperty categoryProperty() {
			return category;
		}

		public String getCategory() {
			return category.get();
		}

		public StringProperty jobEndProperty() {
			return new SimpleStringProperty(jobEnd.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		}

		public String getJobEnd() {
			return (jobEnd.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		}

		public LocalDate jobEndDate() {
			return jobEnd;
		}

		public StringProperty invoiceIDProperty() {
			return invoiceID;
		}

		public String getInvoiceID() {
			return invoiceID.get();
		}

		public StringProperty payedProperty() {
			return payed;
		}

		public String getPayed() {
			return payed.get();
		}

		public String getJobID() {
			return jobID;
		}

		public LocalDate getJobStart() {
			return jobStart;
		}

	}

	public void setModel(Model model) {
		this.model = model;
	}

	public void refresh() {
		initialize();
	}

	@FXML
	public void switchToViewEdit() {
		RSPPtableElement selectedItems = rsppTable.getSelectionModel().getSelectedItem();
		if (selectedItems != null) {
			try {
				Stage stage = new Stage();

				FXMLLoader loader = new FXMLLoader(getClass().getResource("edit.fxml"));
				VBox root = (VBox) loader.load();
				ViewEditController controller = loader.getController();
				model = new Model();
				controller.setRSPP(selectedItems.getJobID(), selectedItems.getJobStart(), selectedItems.jobEndDate());

				Scene scene = new Scene(root);
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				stage.setScene(scene);
				stage.show();

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Attenzione!");
			alert.setHeaderText("Riga RSPP non selezionata");
			alert.setContentText(
					"Come fare?\nPer utilizzare la funziona \"visualizza e modifica\" di questo programma occorre selezionare la riga della tabella e successivamente procedere cliccando il pulsante.");

			alert.showAndWait();
		}

	}
}

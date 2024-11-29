package com.parkit.parkingsystem.integration;


import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static junit.framework.Assert.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

public class ParkingDataBaseIT {

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    private static final String vehicleRegNumber = "ABCDEF";
    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    public static void setUp() {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    public void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    public static void tearDown() {

        dataBasePrepareService.clearDataBaseEntries();
    }

    @Test
    public void testParkingACar() {

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);


        parkingService.processIncomingVehicle();


        Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);

        assertNotNull(ticket);
        assertEquals(vehicleRegNumber, ticket.getVehicleRegNumber());
        assertEquals(0.0, ticket.getPrice());
        assertNotNull(ticket.getInTime());
        assertNull(ticket.getOutTime());

    }


    @Test
    public void testParkingLotExit() {
        testParkingACar();
        dataBasePrepareService.inCar(1);


        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);


        parkingService.processExitingVehicle();

        Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);


        assertNotNull(ticket);
        assertTrue(ticket.getPrice() > 0.0);
        assertTrue(ticket.getOutTime().after(ticket.getInTime()));
    }

    @Test
    public void testParkingLotExitRecurringUser() {
        dataBasePrepareService.inCar(2);
        dataBasePrepareService.carExit();
        dataBasePrepareService.inCar(1);

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        parkingService.processExitingVehicle();


        Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);

        assertNotNull(ticket);
        assertTrue(ticket.getOutTime().after(ticket.getInTime()));
        assertTrue(ticket.getPrice() > 0);
        assertTrue(ticketDAO.getNbTicket(vehicleRegNumber) > 1);

    }

}
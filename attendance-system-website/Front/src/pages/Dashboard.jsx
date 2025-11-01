import React, { useEffect, useState } from "react";
import axios from "axios";
import Layout from "../components/Layout";

function Dashboard() {
    const [stats, setStats] = useState({
        totalClasses: 0,
        totalEtudiants: 0,
        totalAbsences: 0,
        absencesJustifiees: 0,
        alertes: 0,
        pourcentageAbsencesJustifiees: 0,
        etudiantsParClasse: 0
    });

    useEffect(() => {
        const professeurId = localStorage.getItem("professeurId");
        if (!professeurId) return;

        axios.get(`http://localhost:8080/api/dashboard`, {
            params: { professeurId }
        })
            .then((res) => setStats(res.data))
            .catch((err) => console.error(err));
    }, []);


    return (
        <Layout>
            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-6">
                <Card
                    title="Classes"
                    value={stats.totalClasses}
                    subtitle={`${stats.totalEtudiants} étudiants au total`}
                />
                <Card
                    title="Étudiants"
                    value={stats.totalEtudiants}
                    subtitle={`${stats.etudiantsParClasse.toFixed(1)} étudiants par classe en moyenne`}
                />
                <Card
                    title="Absences"
                    value={stats.totalAbsences}
                    subtitle={`${stats.absencesJustifiees} absences justifiées (${stats.pourcentageAbsencesJustifiees.toFixed(0)}%)`}
                />
                <Card
                    title="Alertes"
                    value={stats.alertes}
                    subtitle={`Étudiants ayant dépassé le seuil de 3 absences`}
                />
            </div>
        </Layout>
    );
}

function Card({ title, value, subtitle }) {
    return (
        <div className="bg-white p-4 shadow rounded-xl border">
            <h2 className="text-lg font-semibold">{title}</h2>
            <p className="text-3xl font-bold my-2">{value}</p>
            <p className="text-sm text-gray-500">{subtitle}</p>
        </div>
    );
}

export default Dashboard;
